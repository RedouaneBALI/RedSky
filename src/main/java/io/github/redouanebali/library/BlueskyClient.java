package io.github.redouanebali.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.library.dto.Actor;
import io.github.redouanebali.library.dto.CreateRecordRequest;
import io.github.redouanebali.library.dto.CreateRecordResponse;
import io.github.redouanebali.library.dto.DeleteRecordRequest;
import io.github.redouanebali.library.dto.DeleteRecordResponse;
import io.github.redouanebali.library.dto.GetFollowersResponse;
import io.github.redouanebali.library.dto.GetFollowsResponse;
import io.github.redouanebali.library.dto.GetLikesResponse;
import io.github.redouanebali.library.dto.LoginRequest;
import io.github.redouanebali.library.dto.LoginResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BlueskyClient {

  private static final String       BASE_URL = "https://bsky.social/xrpc/";
  private final        OkHttpClient client;
  private final        ObjectMapper objectMapper;
  private              String       accessToken;
  private              String       did;

  public BlueskyClient() {
    this.client       = SSLUtils.getUnsafeOkHttpClient(); // Utilisation du client sécurisé
    this.objectMapper = new ObjectMapper();
  }

  public String getAtUriFromUrl(String url) throws IOException {
    Pattern pattern = Pattern.compile(".*/profile/([^/]+)/post/([^/]+)");
    Matcher matcher = pattern.matcher(url);

    if (matcher.find()) {
      String handle = matcher.group(1);
      String rkey   = matcher.group(2);

      String did = getDidFromHandle(handle);

      return "at://" + did + "/app.bsky.feed.post/" + rkey;
    }

    throw new IllegalArgumentException("URL invalide : " + url);
  }

  public String getDidFromHandle(String handle) throws IOException {
    Request request = new Request.Builder()
        .url("https://bsky.social/xrpc/com.atproto.identity.resolveHandle?handle=" + handle)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        throw new IOException("Failed to resolve handle: " + response);
      }
      String   responseBody = response.body().string();
      JsonNode jsonNode     = objectMapper.readTree(responseBody);
      return jsonNode.get("did").asText();
    }
  }

  public void login(String identifier, String password) throws IOException {
    LoginRequest loginRequest = new LoginRequest(identifier, password);

    RequestBody body = RequestBody.create(
        objectMapper.writeValueAsString(loginRequest),
        MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
        .url(BASE_URL + "com.atproto.server.createSession")
        .post(body)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Login failed: " + response);
      }

      LoginResponse loginResponse = objectMapper.readValue(
          response.body().string(),
          LoginResponse.class
      );
      this.accessToken = loginResponse.getAccessJwt();
      this.did         = loginResponse.getDid();
    }
  }

  public CreateRecordResponse createRecord(String text) throws IOException {
    CreateRecordRequest createRecordRequest = new CreateRecordRequest(text, did);

    RequestBody body = RequestBody.create(
        objectMapper.writeValueAsString(createRecordRequest),
        MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
        .url(BASE_URL + "com.atproto.repo.createRecord")
        .header("Authorization", "Bearer " + accessToken)
        .post(body)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Post failed: " + response);
      } else if (response.body() == null) {
        throw new IOException("empty body");
      }
      String responseBody = response.body().string();

      return objectMapper.readValue(responseBody, CreateRecordResponse.class);
    }

  }

  public DeleteRecordResponse deleteRecord(String rkey) throws IOException {
    DeleteRecordRequest deleteRecordRequest = new DeleteRecordRequest(did, rkey);

    RequestBody body = RequestBody.create(
        objectMapper.writeValueAsString(deleteRecordRequest),
        MediaType.parse("application/json")
    );

    Request request = new Request.Builder()
        .url(BASE_URL + "com.atproto.repo.deleteRecord")
        .header("Authorization", "Bearer " + accessToken)
        .post(body)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Post failed: " + response);
      } else if (response.body() == null) {
        throw new IOException("empty body");
      }
      String responseBody = response.body().string();
      System.out.println(responseBody);
      return objectMapper.readValue(responseBody, DeleteRecordResponse.class);
    }

  }

  public GetLikesResponse getLikes(String recordUrl) throws IOException {
    String url = BASE_URL + "app.bsky.feed.getLikes?uri=" + getAtUriFromUrl(recordUrl);

    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "Bearer " + accessToken)  // Use the access token
        .get()
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Get likes failed: " + response);
      } else if (response.body() == null) {
        throw new IOException("Empty body in response");
      }

      String responseBody = response.body().string();
      return objectMapper.readValue(responseBody, GetLikesResponse.class);
    }
  }

  public List<Actor> getFollows(String actorId) throws IOException {
    List<Actor> result = new ArrayList<>();
    String      cursor = null;
    do {
      GetFollowsResponse response = getFollows(actorId, cursor);
      result.addAll(response.getFollows());
      cursor = response.getCursor();
    } while (cursor != null);
    return result;
  }

  public GetFollowsResponse getFollows(String actorId, String cursor) throws IOException {
    String url = BASE_URL + "app.bsky.graph.getFollows?actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&cursor=" + cursor;
    }

    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "Bearer " + accessToken)
        .get()
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Get follows failed: " + response);
      } else if (response.body() == null) {
        throw new IOException("Empty body in response");
      }

      String responseBody = response.body().string();
      return objectMapper.readValue(responseBody, GetFollowsResponse.class);
    }
  }

  public List<Actor> getFollowers(String actorId) throws IOException {
    List<Actor> result = new ArrayList<>();
    String      cursor = null;
    do {
      GetFollowersResponse response = getFollowers(actorId, cursor);
      result.addAll(response.getFollowers());
      cursor = response.getCursor();
    } while (cursor != null);
    return result;
  }

  public GetFollowersResponse getFollowers(String actorId, String cursor) throws IOException {
    String url = BASE_URL + "app.bsky.graph.getFollowers?actor=" + actorId;
    if (cursor != null && !cursor.isEmpty()) {
      url += "&cursor=" + cursor;
    }

    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "Bearer " + accessToken)
        .get()
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Get followers failed: " + response);
      } else if (response.body() == null) {
        throw new IOException("Empty body in response");
      }

      String responseBody = response.body().string();
      return objectMapper.readValue(responseBody, GetFollowersResponse.class);
    }
  }

}
