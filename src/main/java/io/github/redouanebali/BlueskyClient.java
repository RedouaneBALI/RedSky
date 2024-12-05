package io.github.redouanebali;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.AtUri;
import io.github.redouanebali.dto.Paginated;
import io.github.redouanebali.dto.actor.Actor;
import io.github.redouanebali.dto.actor.Profiles;
import io.github.redouanebali.dto.follow.FollowersResponse;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.like.LikesResponse.Like;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListResponse;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.login.LoginRequest;
import io.github.redouanebali.dto.login.LoginResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse.Notification;
import io.github.redouanebali.dto.record.BlueskyRecord.Facet;
import io.github.redouanebali.dto.record.CreateRecordRequest;
import io.github.redouanebali.dto.record.CreateRecordResponse;
import io.github.redouanebali.dto.record.DeleteRecordRequest;
import io.github.redouanebali.dto.record.DeleteRecordResponse;
import io.github.redouanebali.dto.record.PostThreadResponse;
import io.github.redouanebali.dto.record.ReasonEnum;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Getter
public class BlueskyClient implements IBlueskyClient {

  private static final String       BASE_URL             = "https://bsky.social/xrpc/";
  private static final String       APPLICATION_JSON     = "application/json";
  private static final String       AUTHORIZATION        = "Authorization";
  private static final String       BEARER               = "Bearer ";
  private static final String       CURSOR               = "cursor";
  private static final int          MAX_RETRIES          = 5;
  private static final int          INITIAL_WAIT_TIME_MS = 1000; // 1 second
  private final        OkHttpClient client;
  private final        ObjectMapper objectMapper;
  private              String       accessToken;
  private              String       did;
  private              String       identifier;

  public BlueskyClient() {
    this.client       = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
    this.objectMapper = new ObjectMapper();
  }

  private <T> Result<T> executeGetRequest(String url, TypeReference<T> typeReference) {
    Request request    = getGetRequest(url);
    int     retryCount = 0;
    int     waitTime   = INITIAL_WAIT_TIME_MS;

    while (retryCount < MAX_RETRIES) {
      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful() && response.body() != null) {
          String responseBody = response.body().string();
          T      result       = objectMapper.readValue(responseBody, typeReference);
          return Result.success(result);
        } else if (response.code() == 429) {
          LOGGER.warn("Rate limit exceeded, retrying after wait time: " + waitTime + " ms");
          Thread.sleep(waitTime);
          waitTime *= 2; // Exponential backoff
          retryCount++;
        } else {
          String errorMessage = "Get request failed: " + response.body().string();
          LOGGER.error(errorMessage);
          return Result.failure(errorMessage);
        }
      } catch (JsonProcessingException e) {
        String errorMessage = "JSON processing exception occurred while getting response from URL: " + url;
        LOGGER.error(errorMessage, e);
        return Result.failure(errorMessage + ": " + e.getMessage());
      } catch (Exception e) {
        String errorMessage = "Exception occurred while getting response from URL: " + url;
        LOGGER.error(errorMessage, e);
        return Result.failure(errorMessage + ": " + e.getMessage());
      }
    }

    String errorMessage = "Max retries reached for GET request to URL: " + url;
    LOGGER.error(errorMessage);
    return Result.failure(errorMessage);
  }


  private <T, R> Result<R> executePostRequest(String url, T requestBodyObject, Class<R> responseType) {
    RequestBody body;
    try {
      body = RequestBody.create(
          objectMapper.writeValueAsString(requestBodyObject),
          MediaType.parse(APPLICATION_JSON)
      );
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while creating request body for URL: " + url;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }

    Request request    = getPostRequest(url, body);
    int     retryCount = 0;
    int     waitTime   = INITIAL_WAIT_TIME_MS;

    while (retryCount < MAX_RETRIES) {
      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful() && response.body() != null) {
          String responseBody = response.body().string();
          R      result       = objectMapper.readValue(responseBody, responseType);
          return Result.success(result);
        } else if (response.code() == 429) {
          LOGGER.warn("Rate limit exceeded, retrying after wait time: " + waitTime + " ms");
          Thread.sleep(waitTime);
          waitTime *= 2; // Exponential backoff
          retryCount++;
        } else {
          String errorMessage = "Post request failed: " + response.body().string();
          LOGGER.error(errorMessage);
          return Result.failure(errorMessage);
        }
      } catch (JsonProcessingException e) {
        String errorMessage = "JSON processing exception occurred while getting response from URL: " + url;
        LOGGER.error(errorMessage, e);
        return Result.failure(errorMessage + ": " + e.getMessage());
      } catch (Exception e) {
        String errorMessage = "Exception occurred while getting response from URL: " + url;
        LOGGER.error(errorMessage, e);
        return Result.failure(errorMessage + ": " + e.getMessage());
      }
    }

    String errorMessage = "Max retries reached for POST request to URL: " + url;
    LOGGER.error(errorMessage);
    return Result.failure(errorMessage);
  }

  private Request getPostRequest(String url, RequestBody body) {
    LOGGER.debug("POST " + url);
    return new Request.Builder()
        .url(url)
        .header(AUTHORIZATION, BEARER + accessToken)
        .post(body)
        .build();
  }

  private Request getGetRequest(String url) {
    LOGGER.debug("GET " + url);
    return new Request.Builder()
        .url(url)
        .header(AUTHORIZATION, BEARER + accessToken)
        .get()
        .build();
  }

  private <T extends Paginated<R>, R> Result<List<R>> getAllPaginatedResults(String initialUrl, Function<String, Result<T>> fetchFunction) {
    List<R> result = new ArrayList<>();
    String  cursor = null;

    do {
      Result<T> pageResult = fetchFunction.apply(cursor);
      if (pageResult.isSuccess()) {
        T page = pageResult.getValue();
        result.addAll(page.retrieveItems());
        cursor = page.getCursor();
      } else {
        String errorMessage = "Failed to get paginated results from URL: " + initialUrl;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + pageResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  public String getAtUriFromUrl(String url) throws IOException {
    Pattern pattern = Pattern.compile(".*/profile/([^/]+)/post/([^/]+)");
    Matcher matcher = pattern.matcher(url);

    if (matcher.find()) {
      String handle = matcher.group(1);
      String rkey   = matcher.group(2);

      String actorDid = getDidFromHandle(handle).getValue();

      return "at://" + actorDid + "/app.bsky.feed.post/" + rkey;
    }

    throw new IllegalArgumentException("URL invalide : " + url);
  }

  public Result<String> getDidFromHandle(String handle) {
    String url = "https://bsky.social/xrpc/com.atproto.identity.resolveHandle?handle=" + handle;
    Result<JsonNode> result = executeGetRequest(url, new TypeReference<JsonNode>() {
    });

    if (result.isSuccess()) {
      JsonNode jsonNode = result.getValue();
      return Result.success(jsonNode.get("did").asText());
    } else {
      return Result.failure(result.getError());
    }
  }


  public Result<Void> login(String identifier, String password) {
    Objects.requireNonNull(identifier, "login identifier cannot be null");
    Objects.requireNonNull(password, "login password cannot be null");
    this.identifier = identifier;
    LoginRequest loginRequest = new LoginRequest(identifier, password);
    String       url          = BASE_URL + "com.atproto.server.createSession";

    Result<LoginResponse> result = executePostRequest(url, loginRequest, LoginResponse.class);

    if (result.isSuccess()) {
      LoginResponse loginResponse = result.getValue();
      this.accessToken = loginResponse.getAccessJwt();
      this.did         = loginResponse.getDid();
      return Result.success(null);
    } else {
      return Result.failure(result.getError());
    }
  }

  public Result<CreateRecordResponse> createRecord(String text) {
    return createRecord(text, null, null, null, null, null);
  }

  public Result<CreateRecordResponse> createRecord(String text, String parentUri, String parentCid, String rootUri, String rootCid) {
    return createRecord(text, parentUri, parentCid, rootUri, rootCid, null);
  }

  @Override
  public Result<CreateRecordResponse> createRecord(final String text,
                                                   final String parentUri,
                                                   final String parentCid,
                                                   final String rootUri,
                                                   final String rootCid,
                                                   final List<Facet> facets) {
    CreateRecordRequest createRecordRequest;
    try {
      if (parentUri == null || parentCid == null || rootUri == null || rootCid == null) {
        createRecordRequest = new CreateRecordRequest(text, did);
      } else if (facets == null) {
        createRecordRequest = new CreateRecordRequest(text, did, parentUri, parentCid, rootUri, rootCid);
      } else {
        createRecordRequest = new CreateRecordRequest(text, did, parentUri, parentCid, rootUri, rootCid, facets);
      }
    } catch (Exception e) {
      String errorMessage = "Exception occurred while creating request object for record: " + text;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }

    RequestBody body;
    try {
      body = RequestBody.create(
          objectMapper.writeValueAsString(createRecordRequest),
          MediaType.parse(APPLICATION_JSON)
      );
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while creating request body: " + text;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }

    Request request = getPostRequest(BASE_URL + "com.atproto.repo.createRecord", body);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Post failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = "empty body";
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }
      String               responseBody         = response.body().string();
      CreateRecordResponse createRecordResponse = objectMapper.readValue(responseBody, CreateRecordResponse.class);
      return Result.success(createRecordResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while creating record: " + text;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while creating record: " + text;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<DeleteRecordResponse> deleteRecord(String rkey) {
    DeleteRecordRequest deleteRecordRequest;
    try {
      deleteRecordRequest = new DeleteRecordRequest(did, rkey);
    } catch (Exception e) {
      String errorMessage = "Exception occurred while creating request object for record: " + rkey;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }

    RequestBody body;
    try {
      body = RequestBody.create(
          objectMapper.writeValueAsString(deleteRecordRequest),
          MediaType.parse(APPLICATION_JSON)
      );
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while creating request body: " + rkey;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }

    Request request = getPostRequest(BASE_URL + "com.atproto.repo.deleteRecord", body);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Post failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = "empty body";
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }
      String               responseBody         = response.body().string();
      DeleteRecordResponse deleteRecordResponse = objectMapper.readValue(responseBody, DeleteRecordResponse.class);
      return Result.success(deleteRecordResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while deleting record: " + rkey;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while deleting record: " + rkey;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<LikesResponse> getLikes(String recordUri, String cursor) {
    String url = BASE_URL + "app.bsky.feed.getLikes?limit=100&uri=" + recordUri;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }
    return executeGetRequest(url, new TypeReference<>() {
    });
  }


  public Result<List<Like>> getAllLikes(String recordUri) {
    return getAllPaginatedResults(
        BASE_URL + "app.bsky.feed.getLikes?uri=" + recordUri,
        cursor -> getLikes(recordUri, cursor)
    );
  }


  public Result<FollowsResponse> getFollows(String actorId, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getFollows?limit=100&actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }
    return executeGetRequest(url, new TypeReference<>() {
    });
  }


  public Result<List<Actor>> getAllFollows(String actorId) {
    return getAllPaginatedResults(
        BASE_URL + "app.bsky.graph.getFollows?limit=100&actor=" + actorId,
        cursor -> getFollows(actorId, cursor)
    );
  }


  public Result<FollowersResponse> getFollowers(String actorId, String cursor) {
    String url = "https://bsky.social/xrpc/app.bsky.graph.getFollowers?limit=100&actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url = url + "&cursor=" + cursor;
    }
    return executeGetRequest(url, new TypeReference<FollowersResponse>() {
    });
  }

  public Result<List<Actor>> getAllFollowers(String actorId) {
    int         retries      = 3;
    List<Actor> allFollowers = new ArrayList<>();
    String      cursor       = null;

    for (int i = 0; i < retries; i++) {
      try {
        Result<FollowersResponse> result = getFollowers(actorId, cursor);
        if (result.isSuccess()) {
          FollowersResponse response = result.getValue();
          allFollowers.addAll(response.getFollowers());
          cursor = response.getCursor();
          if (cursor == null) {
            break;  // No more pages
          }
        } else {
          LOGGER.error("Error while fetching followers: " + result.getError());
        }
      } catch (Exception e) {
        LOGGER.error("Error occurred, retrying...", e);
        if (i == retries - 1) {
          return Result.failure(e.getMessage());  // Return the error after retries
        }
      }
    }
    return Result.success(allFollowers);
  }

  public Result<UserListsResponse> getUserLists(String actorId, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getLists?actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }
    return executeGetRequest(url, new TypeReference<>() {
    });
  }


  public Result<List<UserList>> getAllUserLists(String actorId) {
    return getAllPaginatedResults(
        BASE_URL + "app.bsky.graph.getLists?actor=" + actorId,
        cursor -> getUserLists(actorId, cursor)
    );
  }


  public Result<UserListResponse> getUserListActors(String listUri, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getList?list=" + listUri;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }
    return executeGetRequest(url, new TypeReference<>() {
    });
  }


  public Result<List<Actor>> getAllUserListActors(String listUri) {
    Result<List<UserListResponse.ListItem>> paginatedResult = getAllPaginatedResults(
        BASE_URL + "app.bsky.graph.getList?list=" + listUri,
        cursor -> getUserListActors(listUri, cursor)
    );

    if (paginatedResult.isSuccess()) {
      List<Actor> actors = paginatedResult.getValue().stream()
                                          .map(UserListResponse.ListItem::getSubject)
                                          .collect(Collectors.toList());
      return Result.success(actors);
    } else {
      return Result.failure(paginatedResult.getError());
    }
  }

  @Override
  public Result<ListNotificationsResponse> getListNotifications(final String cursor) {
    String url = BASE_URL + "app.bsky.notification.listNotifications?limit=100";
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }
    TypeReference<ListNotificationsResponse> typeReference = new TypeReference<>() {
    };
    return executeGetRequest(url, typeReference);
  }


  public Result<List<Notification>> getAllListNotifications() {
    return getAllPaginatedResults(BASE_URL + "app.bsky.notification.listNotifications?limit=100", cursor -> getListNotifications(cursor));
  }

  @Override
  public Result<ListNotificationsResponse> getListNotifications(int limit) {
    String url = BASE_URL + "app.bsky.notification.listNotifications?limit=" + limit;
    TypeReference<ListNotificationsResponse> typeReference = new TypeReference<>() {
    };
    return executeGetRequest(url, typeReference);
  }


  @Override
  public Result<PostThreadResponse> getPostThread(String recordUri) {
    String url = BASE_URL + "app.bsky.feed.getPostThread?uri=" + recordUri;
    return executeGetRequest(url, new TypeReference<PostThreadResponse>() {
    });
  }

  public List<Actor> getPostThreadRepliyingActors(String recordUri) {
    Result<PostThreadResponse> result = getPostThread(recordUri);
    if (result.isFailure()) {
      LOGGER.error("error thread not retrieved");
      return new ArrayList<>();
    }
    return result.getValue().getThread().getReplies().stream().map(r -> r.getPost().getAuthor()).toList();
  }


  @Override
  public Result<Actor> getProfile(final String actorHandle) {
    String url = BASE_URL + "app.bsky.actor.getProfile?actor=" + actorHandle;
    return executeGetRequest(url, new TypeReference<Actor>() {
    });
  }


  @Override
  public Result<List<Actor>> getProfiles(final List<String> actorHandles) {
    if (actorHandles == null || actorHandles.isEmpty()) {
      String errorMessage = "The list of actor handles cannot be null or empty.";
      LOGGER.error(errorMessage);
      return Result.failure(errorMessage);
    }

    String handlesQuery = actorHandles.stream()
                                      .map(handle -> URLEncoder.encode(handle, StandardCharsets.UTF_8))
                                      .collect(Collectors.joining("&actors="));

    String url = BASE_URL + "app.bsky.actor.getProfiles?actors=" + handlesQuery;

    Result<Profiles> profilesResult = executeGetRequest(url, new TypeReference<Profiles>() {
    });
    if (profilesResult.isSuccess()) {
      return Result.success(profilesResult.getValue().getProfiles());
    } else {
      return Result.failure(profilesResult.getError());
    }
  }

  public List<Notification> getUnansweredNotifications(List<Notification> notifications) {
    List<Notification> unansweredNotifications = new ArrayList<>();
    for (Notification notification : notifications) {
      if (notification.getReason() == ReasonEnum.MENTION || notification.getReason() == ReasonEnum.REPLY) {
        AtUri                      recordUri = notification.getUri();
        Result<PostThreadResponse> thread    = getPostThread(recordUri.toString());
        if (thread.isFailure()) {
          LOGGER.error("failure when getting the post thread response");
          continue;
        }
        boolean hasResponse = isUserInReplies(thread.getValue(), identifier);
        if (!hasResponse) {
          unansweredNotifications.add(notification);
        }
      }
    }
    return unansweredNotifications;
  }

  private boolean isUserInReplies(PostThreadResponse threadResponse, String userHandle) {
    Objects.requireNonNull(threadResponse, "threadResponse cannot be null");
    Objects.requireNonNull(userHandle, "userHandle cannot be null");

    for (PostThreadResponse.Thread thread : threadResponse.getThread().getReplies()) {
      if (userHandle.equals(thread.getPost().getAuthor().getHandle())) {
        return true;
      }
    }
    return false;
  }
}