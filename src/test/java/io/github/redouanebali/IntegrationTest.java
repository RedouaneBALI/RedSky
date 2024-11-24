package io.github.redouanebali;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.redouanebali.dto.Actor;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
      throw new IllegalArgumentException(
          "Les propriétés bluesky.username et bluesky.password doivent être définies dans le fichier bluesky.properties.");
    }

    BS_CLIENT.login(username, password);
  }

  @Test
  @Disabled("Just for personal use")
  public void createRecordTest() throws IOException {
    CreateRecordResponse createResponse = BS_CLIENT.createRecord("Et hop, je viens d'apprendre à pouvoir supprimer mes posts ! \uD83D\uDE0F ✅");
    assertNotNull(createResponse.getUri());
  }

  @Test
  public void createAndDeleteRecordTest() throws IOException {
    CreateRecordResponse createResponse = BS_CLIENT.createRecord("A supprimer...");
    assertNotNull(createResponse.getUri());
    DeleteRecordResponse deleteResponse = BS_CLIENT.deleteRecord(createResponse.getUri().getRkey());
    assertNotNull(deleteResponse.getCommit().getCid());
    assertNotNull(deleteResponse.getCommit().getRev());
  }

  @Test
  public void testGetAtUriFromUrl() throws IOException {
    String url           = "https://bsky.app/profile/redthebot.bsky.social/post/3lbms7p32qv2m";
    String atUri         = BS_CLIENT.getAtUriFromUrl(url);
    String expectedAtUri = "at://did:plc:g7c7qgmpmyysvrhuvyqi34pf/app.bsky.feed.post/3lbms7p32qv2m";
    assertEquals(expectedAtUri, atUri, "AT-URI should match the expected value.");
  }

  @Test
  @Disabled("Just for personal use")
  public void getAtUriFromUrl() throws IOException {
    LOGGER.info(BS_CLIENT.getAtUriFromUrl("https://bsky.app/profile/redtheone.bsky.social/post/3lbat4rmiqk2h"));
  }

  @Test
  public void getLikes1stPage() throws IOException {
    LikesResponse response = BS_CLIENT.getLikes("https://bsky.app/profile/fabricearfi.bsky.social/post/3lbmql3klhk25", null);
    assertNotNull(response);
    assertFalse(response.getLikes().isEmpty());
    assertTrue(response.getLikes().size() > 15);
  }

  @Test
  public void getAllLikes() throws IOException {
    List<Like> response = BS_CLIENT.getAllLikes("https://bsky.app/profile/fabricearfi.bsky.social/post/3lbmql3klhk25");
    assertNotNull(response);
    assertTrue(response.size() > 150);
  }

  @Test
  public void getFollows1stPage() throws IOException {
    FollowsResponse followsResponse = BS_CLIENT.getFollows("redtheone.bsky.social", null);
    assertNotNull(followsResponse);
    assertFalse(followsResponse.getFollows().isEmpty());
    assertTrue(followsResponse.getFollows().size() > 40);
    assertNotNull(followsResponse.getCursor());
  }

  @Test
  public void getAllFollows() throws IOException {
    List<Actor> follows = BS_CLIENT.getAllFollows("redtheone.bsky.social");
    assertTrue(follows.size() > 70);
    follows.stream().map(Actor::getHandle).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getFollowers1stPage() throws IOException {
    FollowersResponse response = BS_CLIENT.getFollowers("redtheone.bsky.social", null);
    assertNotNull(response);
    assertFalse(response.getFollowers().isEmpty());
    assertTrue(response.getFollowers().size() > 40);
    assertNotNull(response.getCursor());
    response.getFollowers().stream().map(Actor::getHandle).toList().forEach(LOGGER::debug);

  }

  @Test
  public void getAllFollowers() throws IOException {
    List<Actor> response = BS_CLIENT.getAllFollowers("redtheone.bsky.social");
    assertTrue(response.size() > 150);
  }

  @Test
  public void getUserLists1stPage() throws IOException {
    UserListsResponse response = BS_CLIENT.getUserLists("redtheone.bsky.social", null);
    assertNotNull(response);
    assertFalse(response.getLists().isEmpty());
    assertNotNull(response.getLists().getFirst().getUri());
    response.getLists().stream().map(UserList::getName).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getAllUserLists() throws IOException {
    List<UserList> response = BS_CLIENT.getAllUserLists("redtheone.bsky.social");
    assertTrue(response.size() > 1);
    response.stream().map(UserList::getName).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getUserList() throws IOException {
    List<Actor> response = BS_CLIENT.getAllUserList("at://did:plc:tavdd37id64nlh74vaclzuwp/app.bsky.graph.list/3lblye7d6za2z");
    assertNotNull(response);
    response.stream().map(Actor::getHandle).forEach(LOGGER::debug);
  }

  @Test
  public void getListNotifications() throws IOException {
    ListNotificationsResponse response = BS_CLIENT.getListNotifications(null);
    assertNotNull(response);
    assertFalse(response.getNotifications().isEmpty());
  }

  @Test
  public void getAllListNotifications() throws IOException {
    List<Notification> notifications = BS_CLIENT.getAllListNotifications();
    assertFalse(notifications.isEmpty());
    assertTrue(notifications.size() > 50);
    assertNotNull(notifications.getFirst().getReason());
    assertNotNull(notifications.getFirst().getAuthor());
  }
}
