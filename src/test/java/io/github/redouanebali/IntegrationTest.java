package io.github.redouanebali;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.Actor.Actor;
import io.github.redouanebali.dto.follow.FollowersResponse;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.like.LikesResponse.Like;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse.Notification;
import io.github.redouanebali.dto.record.CreateRecordResponse;
import io.github.redouanebali.dto.record.DeleteRecordResponse;
import io.github.redouanebali.dto.record.PostThreadResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
public class IntegrationTest {

  private static final BlueskyClient BS_CLIENT = new BlueskyClient();

  @BeforeAll
  public static void init() throws IOException {
    Properties properties = new Properties();
    try (InputStream input = new FileInputStream("bluesky.properties")) {
      properties.load(input);
    }

    String username = properties.getProperty("bluesky.username");
    String password = properties.getProperty("bluesky.password");

    if (username == null || password == null) {
      throw new IllegalArgumentException("no credentials found");
    }

    BS_CLIENT.login(username, password);
  }

  @Test
  @Disabled("Just for personal use")
  public void getAtUriFromUrl() throws IOException {
    LOGGER.info(BS_CLIENT.getAtUriFromUrl("https://bsky.app/profile/redtheone.bsky.social/post/3lbushrpzqk2h"));
  }

  @Test
  @Disabled("Just for personal use")
  public void createRecordTest() {
    CreateRecordResponse
        createResponse =
        BS_CLIENT.createRecord("Et hop, je viens d'apprendre à pouvoir supprimer mes posts ! \uD83D\uDE0F ✅").getValue();
    assertNotNull(createResponse.getUri());
  }

  @Test
  public void createAndDeleteRecordTest() {
    CreateRecordResponse createResponse = BS_CLIENT.createRecord("A supprimer...").getValue();
    assertNotNull(createResponse.getUri());
    DeleteRecordResponse deleteResponse = BS_CLIENT.deleteRecord(createResponse.getUri().getRkey()).getValue();
    assertNotNull(deleteResponse.getCommit().getCid());
    assertNotNull(deleteResponse.getCommit().getRev());
  }

  @Test
  public void getAtUriFromUrlTest() throws IOException {
    String url           = "https://bsky.app/profile/redthebot.bsky.social/post/3lbms7p32qv2m";
    String atUri         = BS_CLIENT.getAtUriFromUrl(url);
    String expectedAtUri = "at://did:plc:g7c7qgmpmyysvrhuvyqi34pf/app.bsky.feed.post/3lbms7p32qv2m";
    assertEquals(expectedAtUri, atUri, "AT-URI should match the expected value.");
  }

  @Test
  public void getLikes1stPageTest() throws IOException {
    LikesResponse
        response =
        BS_CLIENT.getLikes(BS_CLIENT.getAtUriFromUrl("https://bsky.app/profile/fabricearfi.bsky.social/post/3lbmql3klhk25"), null)
                 .getValue();
    assertNotNull(response);
    assertFalse(response.getLikes().isEmpty());
    assertTrue(response.getLikes().size() > 15);
  }

  @Test
  public void getAllLikesTest() throws IOException {
    List<Like>
        response =
        BS_CLIENT.getAllLikes(BS_CLIENT.getAtUriFromUrl("https://bsky.app/profile/fabricearfi.bsky.social/post/3lbmql3klhk25")).getValue();
    assertNotNull(response);
    assertTrue(response.size() > 150);
  }

  @Test
  public void getFollows1stPageTest() {
    FollowsResponse followsResponse = BS_CLIENT.getFollows("sreyephleung.bsky.social", null).getValue();
    assertNotNull(followsResponse);
    assertFalse(followsResponse.getFollows().isEmpty());
    assertEquals(100, followsResponse.getFollows().size());
    assertNotNull(followsResponse.getCursor());
  }

  @Test
  public void getAllFollowsTest() {
    List<Actor> follows = BS_CLIENT.getAllFollows("redtheone.bsky.social").getValue();
    assertTrue(follows.size() > 70);
    follows.stream().map(Actor::getHandle).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getFollowers1stPageTest() {
    FollowersResponse response = BS_CLIENT.getFollowers("redtheone.bsky.social", null).getValue();
    assertNotNull(response);
    assertFalse(response.getFollowers().isEmpty());
    assertTrue(response.getFollowers().size() > 40);
    assertNotNull(response.getCursor());
    response.getFollowers().stream().map(Actor::getHandle).toList().forEach(LOGGER::debug);

  }

  @Test
  public void getAllFollowersTest() {
    List<Actor> response = BS_CLIENT.getAllFollowers("redtheone.bsky.social").getValue();
    assertTrue(response.size() > 150);
  }

  @Test
  public void getUserLists1stPageTest() {
    UserListsResponse response = BS_CLIENT.getUserLists("redtheone.bsky.social", null).getValue();
    assertNotNull(response);
    assertFalse(response.getLists().isEmpty());
    assertNotNull(response.getLists().getFirst().getUri());
    response.getLists().stream().map(UserList::getName).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getAllUserListsTest() {
    List<UserList> response = BS_CLIENT.getAllUserLists("redtheone.bsky.social").getValue();
    assertTrue(response.size() > 0);
    response.stream().map(UserList::getName).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getUserListTest() {
    List<Actor> response = BS_CLIENT.getAllUserListActors("at://did:plc:g7c7qgmpmyysvrhuvyqi34pf/app.bsky.graph.list/3lbs6quzmyf26").getValue();
    assertNotNull(response);
    response.stream().map(Actor::getHandle).forEach(LOGGER::debug);
  }

  @Test
  public void getListNotificationsTest() {
    ListNotificationsResponse response = BS_CLIENT.getListNotifications(null).getValue();
    assertNotNull(response);
    assertFalse(response.getNotifications().isEmpty());
  }

  @Test
  public void getAllListNotificationsTest() {
    List<Notification> notifications = BS_CLIENT.getAllListNotifications().getValue();
    assertFalse(notifications.isEmpty());
    assertTrue(notifications.size() > 50);
    assertNotNull(notifications.getFirst().getReason());
    assertNotNull(notifications.getFirst().getAuthor());
  }


  @Test
  public void getPostThreadTest() throws IOException {
    PostThreadResponse
        response =
        BS_CLIENT.getPostThread(BS_CLIENT.getAtUriFromUrl("https://bsky.app/profile/redthebot.bsky.social/post/3lbms7p32qv2m")).getValue();
    assertEquals("redthebot.bsky.social", response.getThread().getPost().getAuthor().getHandle());
    assertEquals("Test n°526", response.getThread().getPost().getRecord().getText());
    assertTrue(response.getThread().getPost().getLikeCount() > 0);
  }

  @Test
  public void getProfileTest() {
    Actor response = BS_CLIENT.getProfile("redthebot.bsky.social").getValue();
    assertEquals("redthebot.bsky.social", response.getHandle());
    assertTrue(response.getFollowersCount() > 10);
    assertTrue(response.getFollowsCount() > 5);
    assertTrue(response.getPostsCount() > 5);
    assertTrue(response.getDisplayName().contains("Red"));
  }

  @Test
  public void getProfilesTest() {
    List<Actor> response = BS_CLIENT.getProfiles(List.of("redthebot.bsky.social", "redthebot.bsky.social")).getValue();
    assertEquals(2, response.size());
    assertTrue(response.getFirst().getFollowsCount() > 5);
    assertTrue(response.getLast().getPostsCount() > 5);
  }

  @Test
  public void getUnansweredNotificationsTest() throws IOException {
    Path                      jsonFilePath            = Path.of("src/test/resources/listNotifications.json");
    String                    jsonContent             = Files.readString(jsonFilePath);
    ListNotificationsResponse listNotifications       = new ObjectMapper().readValue(jsonContent, ListNotificationsResponse.class);
    List<Notification>        unansweredNotifications = BS_CLIENT.getUnansweredNotifications(listNotifications.getNotifications());
    assertFalse(unansweredNotifications.isEmpty());
  }
}
