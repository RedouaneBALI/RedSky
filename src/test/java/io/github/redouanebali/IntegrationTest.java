package io.github.redouanebali;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.actor.Actor;
import io.github.redouanebali.dto.follow.FollowersResponse;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.like.LikesResponse.Like;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse.Notification;
import io.github.redouanebali.dto.record.BlueskyRecord.Facet;
import io.github.redouanebali.dto.record.BlueskyRecord.Facet.Feature;
import io.github.redouanebali.dto.record.BlueskyRecord.Facet.Index;
import io.github.redouanebali.dto.record.CreateRecordResponse;
import io.github.redouanebali.dto.record.DeleteRecordResponse;
import io.github.redouanebali.dto.record.FeatureType;
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

  private static final BlueskyClient CLIENT = new BlueskyClient();

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

    CLIENT.login(username, password);
  }

  @Test
  @Disabled("Just for personal use")
  public void getAtUriFromUrl() throws IOException {
    LOGGER.info(CLIENT.getAtUriFromUrl("https://bsky.app/profile/redtheone.bsky.social/post/3lbushrpzqk2h"));
  }

  @Test
  @Disabled("Just for personal use")
  public void createRecordTest() {
    CreateRecordResponse
        createResponse =
        CLIENT.createRecord("Et hop, je viens d'apprendre à pouvoir supprimer mes posts ! \uD83D\uDE0F ✅").getValue();
    assertNotNull(createResponse.getUri());
  }

  @Test
  public void createAndDeleteRecordTest() {
    CreateRecordResponse createResponse = CLIENT.createRecord("A supprimer...").getValue();
    assertNotNull(createResponse.getUri());
    assertEquals("valid", createResponse.getValidationStatus());
    DeleteRecordResponse deleteResponse = CLIENT.deleteRecord(createResponse.getUri().getRkey()).getValue();
    assertNotNull(deleteResponse.getCommit().getCid());
    assertNotNull(deleteResponse.getCommit().getRev());
  }

  @Test
  public void createAndDeleteRecordTestWithFacets() throws IOException {
    PostThreadResponse
        postThreadResponse =
        CLIENT.getPostThread(CLIENT.getAtUriFromUrl("https://bsky.app/profile/redtheone.bsky.social/post/3lbat4rmiqk2h")).getValue();
    Facet facet = Facet.builder()
                       .features(List.of(Feature.builder()
                                                .did("did:plc:tavdd37id64nlh74vaclzuwp")
                                                .type(FeatureType.MENTION)
                                                .build()))
                       .index(new Index(22, 32))
                       .build();
    List<Facet> facets = List.of(facet);
    CreateRecordResponse createResponse = CLIENT.createRecord("Ceci est un test pour @redtheone 3...",
                                                              postThreadResponse.getThread().getPost().getUri().toString(),
                                                              postThreadResponse.getThread().getPost().getCid(),
                                                              postThreadResponse.getThread().getPost().getUri().toString(),
                                                              postThreadResponse.getThread().getPost().getCid(),
                                                              facets)

                                                .getValue();

    assertNotNull(createResponse.getUri());
    assertEquals("valid", createResponse.getValidationStatus());
    DeleteRecordResponse deleteResponse = CLIENT.deleteRecord(createResponse.getUri().getRkey()).getValue();
    assertNotNull(deleteResponse.getCommit().getCid());
    assertNotNull(deleteResponse.getCommit().getRev());
  }

  @Test
  public void getAtUriFromUrlTest() throws IOException {
    String url           = "https://bsky.app/profile/redthebot.bsky.social/post/3lbms7p32qv2m";
    String atUri         = CLIENT.getAtUriFromUrl(url);
    String expectedAtUri = "at://did:plc:g7c7qgmpmyysvrhuvyqi34pf/app.bsky.feed.post/3lbms7p32qv2m";
    assertEquals(expectedAtUri, atUri, "AT-URI should match the expected value.");
  }

  @Test
  public void getLikes1stPageTest() throws IOException {
    LikesResponse
        response =
        CLIENT.getLikes(CLIENT.getAtUriFromUrl("https://bsky.app/profile/fabricearfi.bsky.social/post/3lbmql3klhk25"), null)
              .getValue();
    assertNotNull(response);
    assertFalse(response.getLikes().isEmpty());
    assertTrue(response.getLikes().size() > 15);
  }

  @Test
  public void getAllLikesTest() throws IOException {
    List<Like>
        response =
        CLIENT.getAllLikes(CLIENT.getAtUriFromUrl("https://bsky.app/profile/fabricearfi.bsky.social/post/3lbmql3klhk25")).getValue();
    assertNotNull(response);
    assertTrue(response.size() > 150);
  }

  @Test
  public void getFollows1stPageTest() {
    FollowsResponse followsResponse = CLIENT.getFollows("sreyephleung.bsky.social", null).getValue();
    assertNotNull(followsResponse);
    assertFalse(followsResponse.getFollows().isEmpty());
    assertEquals(100, followsResponse.getFollows().size());
    assertNotNull(followsResponse.getCursor());
  }

  @Test
  public void getAllFollowsTest() {
    List<Actor> follows = CLIENT.getAllFollows("redtheone.bsky.social").getValue();
    assertTrue(follows.size() > 70);
    follows.stream().map(Actor::getHandle).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getFollowers1stPageTest() {
    FollowersResponse response = CLIENT.getFollowers("redtheone.bsky.social", null).getValue();
    assertNotNull(response);
    assertFalse(response.getFollowers().isEmpty());
    assertTrue(response.getFollowers().size() > 40);
    assertNotNull(response.getCursor());
    response.getFollowers().stream().map(Actor::getHandle).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getAllFollowersTest() {
    List<Actor> response = CLIENT.getAllFollowers("redtheone.bsky.social").getValue();
    assertTrue(response.size() > 150);
  }

  @Test
  public void getUserLists1stPageTest() {
    UserListsResponse response = CLIENT.getUserLists("redthebot.bsky.social", null).getValue();
    assertNotNull(response);
    assertFalse(response.getLists().isEmpty());
    assertNotNull(response.getLists().getFirst().getUri());
    response.getLists().stream().map(UserList::getName).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getAllUserListsTest() {
    List<UserList> response = CLIENT.getAllUserLists("redthebot.bsky.social").getValue();
    assertTrue(response.size() > 0);
    response.stream().map(UserList::getName).toList().forEach(LOGGER::debug);
  }

  @Test
  public void getUserListTest() {
    Result<List<Actor>> response = CLIENT.getAllUserListActors("at://did:plc:g7c7qgmpmyysvrhuvyqi34pf/app.bsky.graph.list/3lbs6quzmyf26");
    assertNotNull(response);
    response.getValue().stream().map(Actor::getHandle).forEach(LOGGER::debug);
  }

  @Test
  public void getListNotificationsTest() {
    ListNotificationsResponse response = CLIENT.getListNotifications(null).getValue();
    assertNotNull(response);
    assertFalse(response.getNotifications().isEmpty());
  }

  @Test
  public void getAllListNotificationsTest() {
    List<Notification> notifications = CLIENT.getAllListNotifications().getValue();
    assertFalse(notifications.isEmpty());
    assertTrue(notifications.size() > 50);
    assertNotNull(notifications.getFirst().getReason());
    assertNotNull(notifications.getFirst().getAuthor());
  }


  @Test
  public void getPostThreadTest() throws IOException {
    PostThreadResponse
        response =
        CLIENT.getPostThread(CLIENT.getAtUriFromUrl("https://bsky.app/profile/redthebot.bsky.social/post/3lbms7p32qv2m")).getValue();
    assertEquals("redthebot.bsky.social", response.getThread().getPost().getAuthor().getHandle());
    assertEquals("Test n°526", response.getThread().getPost().getRecord().getText());
    assertTrue(response.getThread().getPost().getLikeCount() > 0);
  }

  @Test
  public void getPostThreadTest2() {
    Result<PostThreadResponse>
        response =
        CLIENT.getPostThread("at://did:plc:7x3osrk55wcuveawdk63dmjt/app.bsky.feed.post/3lcdw6bf7ds2f");
    assertTrue(response.isSuccess());
    assertEquals("dejanthe.bsky.social", response.getValue().getThread().getPost().getAuthor().getHandle());
  }


  @Test
  public void getPostThreadReplyingActorsTest() throws IOException {
    List<Actor>
        actors =
        CLIENT.getPostThreadRepliyingActors(CLIENT.getAtUriFromUrl("https://bsky.app/profile/redthebot.bsky.social/post/3lbsabnst2s2b"));
    assertNotNull(actors);
    assertTrue(actors.size() >= 5);
    assertTrue(actors.stream().map(Actor::getHandle).anyMatch(h -> h.equals("redtheone.bsky.social")));
  }

  @Test
  public void getProfileTest() {
    Actor response = CLIENT.getProfile("redthebot.bsky.social").getValue();
    assertEquals("redthebot.bsky.social", response.getHandle());
    assertTrue(response.getFollowersCount() > 10);
    assertTrue(response.getFollowsCount() > 5);
    assertTrue(response.getPostsCount() > 5);
    assertTrue(response.getDisplayName().contains("Red"));
  }

  @Test
  public void getProfilesTest() {
    List<Actor> response = CLIENT.getProfiles(List.of("redthebot.bsky.social", "redthebot.bsky.social")).getValue();
    assertEquals(2, response.size());
    assertTrue(response.getFirst().getFollowsCount() > 5);
    assertTrue(response.getLast().getPostsCount() > 5);
  }

  @Test
  public void getUnansweredNotificationsFromJsonTest() throws IOException {
    Path                      jsonFilePath            = Path.of("src/test/resources/listNotifications.json");
    String                    jsonContent             = Files.readString(jsonFilePath);
    ListNotificationsResponse listNotifications       = new ObjectMapper().readValue(jsonContent, ListNotificationsResponse.class);
    List<Notification>        unansweredNotifications = CLIENT.getUnansweredNotifications(listNotifications.getNotifications());
    assertFalse(unansweredNotifications.isEmpty());
  }

  @Test
  public void getUnansweredNotificationsRealNotifsTest() {
    ListNotificationsResponse listNotifications       = CLIENT.getListNotifications(null).getValue();
    List<Notification>        unansweredNotifications = CLIENT.getUnansweredNotifications(listNotifications.getNotifications());
    assertFalse(unansweredNotifications.isEmpty());
  }
}
