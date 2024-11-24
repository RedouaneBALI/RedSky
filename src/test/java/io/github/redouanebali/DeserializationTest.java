package io.github.redouanebali;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.Actor;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListResponse;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.record.PostThreadResponse;
import io.github.redouanebali.dto.record.ReasonEnum;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class DeserializationTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testDeserializeGetLikesResponse() throws IOException {
    Path          jsonFilePath = Path.of("src/test/resources/likesResponse.json");
    String        jsonContent  = Files.readString(jsonFilePath);
    LikesResponse response     = objectMapper.readValue(jsonContent, LikesResponse.class);
    assertNotNull(response, "Response object should not be null");
    assertNotNull(response.getUri(), "URI should not be null");
    assertNotNull(response.getLikes(), "Likes list should not be null");
    assertFalse(response.getLikes().isEmpty(), "Likes list should not be empty");
    assertEquals(16, response.getLikes().size());
    LikesResponse.Like firstLike = response.getLikes().getFirst();
    assertNotNull(firstLike.getActor(), "Actor should not be null");
    assertEquals("xouyou.bsky.social", firstLike.getActor().getHandle(), "Handle should match expected value");
  }

  @Test
  public void testDeserializeGetFollows() throws IOException {
    Path            jsonFilePath = Path.of("src/test/resources/follows.json");
    String          jsonContent  = Files.readString(jsonFilePath);
    FollowsResponse response     = objectMapper.readValue(jsonContent, FollowsResponse.class);
    assertNotNull(response);
    assertEquals("3kbxxx7wual2f", response.getCursor());
    assertEquals(47, response.getFollows().size());
  }

  @Test
  public void testDeserializeGetUserLists() throws IOException {
    Path              jsonFilePath = Path.of("src/test/resources/userLists.json");
    String            jsonContent  = Files.readString(jsonFilePath);
    UserListsResponse response     = objectMapper.readValue(jsonContent, UserListsResponse.class);
    assertNotNull(response);
    assertEquals(2, response.getLists().size());
    UserList userList = response.getLists().getLast();
    assertNotNull(userList.getUri());
    assertTrue(userList.getListItemCount() > 0);
  }

  @Test
  public void testDeserializeGetUserList() throws IOException {
    Path             jsonFilePath = Path.of("src/test/resources/userList.json");
    String           jsonContent  = Files.readString(jsonFilePath);
    UserListResponse userList     = objectMapper.readValue(jsonContent, UserListResponse.class);
    assertNotNull(userList.getList());
    assertNotNull(userList.getItems());
    Actor actor = userList.getItems().getFirst().getSubject();
    assertNotNull(actor.getHandle());
  }

  @Test
  public void testDeserializeGetListNostifications() throws IOException {
    Path                      jsonFilePath      = Path.of("src/test/resources/listNotifications.json");
    String                    jsonContent       = Files.readString(jsonFilePath);
    ListNotificationsResponse listNotifications = objectMapper.readValue(jsonContent, ListNotificationsResponse.class);
    assertNotNull(listNotifications);
    assertNotNull(listNotifications.getCursor());
    assertEquals(50, listNotifications.getNotifications().size());
    assertNotNull(listNotifications.getNotifications().getFirst().getAuthor().getHandle());
    assertEquals(ReasonEnum.MENTION, listNotifications.getNotifications().getFirst().getReason());
  }

  @Test
  public void testDeserializeGetPostThread() throws IOException {
    Path               jsonFilePath = Path.of("src/test/resources/postThreadResponse.json");
    String             jsonContent  = Files.readString(jsonFilePath);
    PostThreadResponse postThread   = objectMapper.readValue(jsonContent, PostThreadResponse.class);
    assertNotNull(postThread);
    assertNotNull(postThread.getThread());
    assertNotNull(postThread.getThread().getPost().getAuthor().getHandle());
    assertNotNull(postThread.getThread().getPost().getRecord().getText());
    assertEquals(8, postThread.getThread().getReplies().size());
  }

}
