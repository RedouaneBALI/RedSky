package io.github.redouanebali.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.SSLUtils;
import io.github.redouanebali.library.dto.CreateRecordRequest;
import io.github.redouanebali.library.dto.CreateRecordResponse;
import io.github.redouanebali.library.dto.DeleteRecordRequest;
import io.github.redouanebali.library.dto.DeleteRecordResponse;
import io.github.redouanebali.library.dto.LoginRequest;
import io.github.redouanebali.library.dto.LoginResponse;
import java.io.IOException;
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


}
