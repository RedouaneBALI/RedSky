package io.github.redouanebali;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.Actor.Actor;
import io.github.redouanebali.dto.Actor.Profiles;
import io.github.redouanebali.dto.AtUri;
import io.github.redouanebali.dto.follow.FollowersResponse;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.like.LikesResponse.Like;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListResponse;
import io.github.redouanebali.dto.lists.UserListResponse.ListItem;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.login.LoginRequest;
import io.github.redouanebali.dto.login.LoginResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse.Notification;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class BlueskyClient implements IBlueskyClient {

  private static final String       BASE_URL         = "https://bsky.social/xrpc/";
  private static final String       APPLICATION_JSON = "application/json";
  private static final String       AUTHORIZATION    = "Authorization";
  private static final String       BEARER           = "Bearer ";
  private static final String       CURSOR           = "cursor";
  private static final String       EMPTY_BODY       = "Empty body in response";
  private final        OkHttpClient client;
  private final        ObjectMapper objectMapper;
  private              String       accessToken;
  private              String       did;
  private              String       identifier;

  public BlueskyClient() {
    this.client       = SSLUtils.getUnsafeOkHttpClient(); // to be replaced by new OkHttpClient(); out of local scope
    this.objectMapper = new ObjectMapper();
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
    Request request = new Request.Builder()
        .url("https://bsky.social/xrpc/com.atproto.identity.resolveHandle?handle=" + handle)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        String errorMessage = "Failed to resolve handle: " + response;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }
      String   responseBody = response.body().string();
      JsonNode jsonNode     = objectMapper.readTree(responseBody);
      return Result.success(jsonNode.get("did").asText());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while resolving handle: " + handle;
      LOGGER.error(errorMessage);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<Void> login(String identifier, String password) throws JsonProcessingException {
    this.identifier = identifier;
    LoginRequest loginRequest = new LoginRequest(identifier, password);

    RequestBody body = RequestBody.create(
        objectMapper.writeValueAsString(loginRequest),
        MediaType.parse(APPLICATION_JSON)
    );

    Request request = new Request.Builder()
        .url(BASE_URL + "com.atproto.server.createSession")
        .post(body)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Login failed: " + response;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      LoginResponse loginResponse = objectMapper.readValue(
          response.body().string(),
          LoginResponse.class
      );
      this.accessToken = loginResponse.getAccessJwt();
      this.did         = loginResponse.getDid();
      return Result.success(null);
    } catch (Exception e) {
      String errorMessage = "Exception occurred during login for identifier: " + identifier;
      LOGGER.error(errorMessage);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<CreateRecordResponse> createRecord(String text) {
    CreateRecordRequest createRecordRequest;
    try {
      createRecordRequest = new CreateRecordRequest(text, did);
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
    String url = BASE_URL + "app.bsky.feed.getLikes?uri=" + recordUri;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }
    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Get likes failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = EMPTY_BODY;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String        responseBody  = response.body().string();
      LikesResponse likesResponse = objectMapper.readValue(responseBody, LikesResponse.class);
      return Result.success(likesResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while getting likes for URI: " + recordUri;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while getting likes for URI: " + recordUri;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<List<Like>> getAllLikes(String recordUri) {
    List<Like> result = new ArrayList<>();
    String     cursor = null;

    do {
      Result<LikesResponse> likesResponseResult = getLikes(recordUri, cursor);
      if (likesResponseResult.isSuccess()) {
        LikesResponse likesResponse = likesResponseResult.getValue();
        result.addAll(likesResponse.getLikes());
        cursor = likesResponse.getCursor();
      } else {
        String errorMessage = "Failed to get all likes for URI: " + recordUri;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + likesResponseResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  public Result<FollowsResponse> getFollows(String actorId, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getFollows?limit=100&actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Get follows failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = EMPTY_BODY;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String          responseBody    = response.body().string();
      FollowsResponse followsResponse = objectMapper.readValue(responseBody, FollowsResponse.class);
      return Result.success(followsResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while getting follows for actor ID: " + actorId;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while getting follows for actor ID: " + actorId;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<List<Actor>> getAllFollows(String actorId) {
    List<Actor> result = new ArrayList<>();
    String      cursor = null;

    do {
      Result<FollowsResponse> followsResponseResult = getFollows(actorId, cursor);
      if (followsResponseResult.isSuccess()) {
        FollowsResponse followsResponse = followsResponseResult.getValue();
        result.addAll(followsResponse.getFollows());
        cursor = followsResponse.getCursor();
      } else {
        String errorMessage = "Failed to get all follows for actor ID: " + actorId;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + followsResponseResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  public Result<FollowersResponse> getFollowers(String actorId, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getFollowers?limit=100&actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Get followers failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = EMPTY_BODY;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String            responseBody      = response.body().string();
      FollowersResponse followersResponse = objectMapper.readValue(responseBody, FollowersResponse.class);
      return Result.success(followersResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while getting followers for actor ID: " + actorId;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while getting followers for actor ID: " + actorId;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<List<Actor>> getAllFollowers(String actorId) {
    List<Actor> result = new ArrayList<>();
    String      cursor = null;

    do {
      Result<FollowersResponse> followersResponseResult = getFollowers(actorId, cursor);
      if (followersResponseResult.isSuccess()) {
        FollowersResponse followersResponse = followersResponseResult.getValue();
        result.addAll(followersResponse.getFollowers());
        cursor = followersResponse.getCursor();
      } else {
        String errorMessage = "Failed to get all followers for actor ID: " + actorId;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + followersResponseResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  public Result<UserListsResponse> getUserLists(String actorId, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getLists?actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Get user lists failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = EMPTY_BODY;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String            responseBody      = response.body().string();
      UserListsResponse userListsResponse = objectMapper.readValue(responseBody, UserListsResponse.class);
      return Result.success(userListsResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while getting user lists for actor ID: " + actorId;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while getting user lists for actor ID: " + actorId;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<List<UserList>> getAllUserLists(String actorId) {
    List<UserList> result = new ArrayList<>();
    String         cursor = null;

    do {
      Result<UserListsResponse> userListsResponseResult = getUserLists(actorId, cursor);
      if (userListsResponseResult.isSuccess()) {
        UserListsResponse userListsResponse = userListsResponseResult.getValue();
        result.addAll(userListsResponse.getLists());
        cursor = userListsResponse.getCursor();
      } else {
        String errorMessage = "Failed to get all user lists for actor ID: " + actorId;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + userListsResponseResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  public Result<UserListResponse> getUserListActors(String listUri, String cursor) {
    String url = BASE_URL + "app.bsky.graph.getList?list=" + listUri;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Get user list failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = EMPTY_BODY;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String           responseBody     = response.body().string();
      UserListResponse userListResponse = objectMapper.readValue(responseBody, UserListResponse.class);
      return Result.success(userListResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while getting user list actors for list URI: " + listUri;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while getting user list actors for list URI: " + listUri;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public Result<List<Actor>> getAllUserListActors(String listUri) {
    List<Actor> result = new ArrayList<>();
    String      cursor = null;

    do {
      Result<UserListResponse> userListResponseResult = getUserListActors(listUri, cursor);
      if (userListResponseResult.isSuccess()) {
        UserListResponse userListResponse = userListResponseResult.getValue();
        result.addAll(userListResponse.getItems().stream().map(ListItem::getSubject).toList());
        cursor = userListResponse.getCursor();
      } else {
        String errorMessage = "Failed to get all user list actors for list URI: " + listUri;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + userListResponseResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  @Override
  public Result<ListNotificationsResponse> getListNotifications(final String cursor) {
    String url = BASE_URL + "app.bsky.notification.listNotifications?limit=100";
    if (cursor != null && !cursor.isEmpty()) {
      url += "&" + CURSOR + "=" + cursor;
    }

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorMessage = "Get list notifications failed: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      } else if (response.body() == null) {
        String errorMessage = EMPTY_BODY;
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String                    responseBody              = response.body().string();
      ListNotificationsResponse listNotificationsResponse = objectMapper.readValue(responseBody, ListNotificationsResponse.class);
      return Result.success(listNotificationsResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while getting list notifications";
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while getting list notifications";
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  @Override
  public Result<List<Notification>> getAllListNotifications() {
    List<Notification> result = new ArrayList<>();
    String             cursor = null;

    do {
      Result<ListNotificationsResponse> listNotificationsResponseResult = getListNotifications(cursor);
      if (listNotificationsResponseResult.isSuccess()) {
        ListNotificationsResponse listNotificationsResponse = listNotificationsResponseResult.getValue();
        result.addAll(listNotificationsResponse.getNotifications());
        cursor = listNotificationsResponse.getCursor();
        LOGGER.info("calling with cursor = " + cursor);
      } else {
        String errorMessage = "Failed to get all list notifications";
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage + ": " + listNotificationsResponseResult.getError());
      }
    } while (cursor != null);

    return Result.success(result);
  }


  @Override
  public Result<PostThreadResponse> getPostThread(String recordUri) {
    String url = BASE_URL + "app.bsky.feed.getPostThread?uri=" + recordUri;

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        String errorMessage = "Failed to fetch post thread: " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }

      String             responseBody       = response.body().string();
      PostThreadResponse postThreadResponse = objectMapper.readValue(responseBody, PostThreadResponse.class);
      return Result.success(postThreadResponse);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while fetching post thread for URI: " + recordUri;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while fetching post thread for URI: " + recordUri;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  @Override
  public Result<Actor> getProfile(final String actorHandle) {
    String url = BASE_URL + "app.bsky.actor.getProfile?actor=" + actorHandle;

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        String errorMessage = "Failed to fetch profile : " + response.body().string();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }
      String responseBody = response.body().string();
      Actor  actorProfile = objectMapper.readValue(responseBody, Actor.class);
      return Result.success(actorProfile);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while fetching profile for actor handle: " + actorHandle;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while fetching profile for actor handle: " + actorHandle;
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
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

    Request request = getGetRequest(url);

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        String errorMessage = "Failed to fetch profiles: " + response.message();
        LOGGER.error(errorMessage);
        return Result.failure(errorMessage);
      }
      String      responseBody = response.body().string();
      List<Actor> profiles     = objectMapper.readValue(responseBody, Profiles.class).getProfiles();
      return Result.success(profiles);
    } catch (JsonProcessingException e) {
      String errorMessage = "JSON processing exception occurred while fetching profiles";
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    } catch (Exception e) {
      String errorMessage = "Exception occurred while fetching profiles";
      LOGGER.error(errorMessage, e);
      return Result.failure(errorMessage + ": " + e.getMessage());
    }
  }


  public List<Notification> getUnansweredNotifications(List<Notification> notifications) throws IOException {
    List<Notification> unansweredNotifications = new ArrayList<>();
    for (Notification notification : notifications) {
      if (notification.getReason() == ReasonEnum.MENTION || notification.getReason() == ReasonEnum.REPLY) {
        AtUri                      recordUri   = notification.getUri();
        Result<PostThreadResponse> thread      = getPostThread(recordUri.toString());
        boolean                    hasResponse = isUserInReplies(thread.getValue(), identifier);
        if (!hasResponse) {
          unansweredNotifications.add(notification);
        }
      }
    }
    return unansweredNotifications;
  }

  private boolean isUserInReplies(PostThreadResponse threadResponse, String userHandle) {
    if (threadResponse == null || threadResponse.getThread() == null) {
      LOGGER.error("threadResponse or thread null");
      return false;
    }
    for (PostThreadResponse.Thread thread : threadResponse.getThread().getReplies()) {

      if (userHandle.equals(thread.getPost().getAuthor().getHandle())) {
        return true;
      }
    }
    return false;
  }
}