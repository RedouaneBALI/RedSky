import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.redouanebali.library.BlueskyClient;
import io.github.redouanebali.library.dto.Actor;
import io.github.redouanebali.library.dto.CreateRecordResponse;
import io.github.redouanebali.library.dto.DeleteRecordResponse;
import io.github.redouanebali.library.dto.GetFollowersResponse;
import io.github.redouanebali.library.dto.GetFollowsResponse;
import io.github.redouanebali.library.dto.GetLikesResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
  @Disabled
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
    System.out.println(deleteResponse);
  }

  @Test
  public void testGetAtUriFromUrl() throws IOException {
    String url           = "https://bsky.app/profile/redthebot.bsky.social/post/3lbms7p32qv2m";
    String atUri         = BS_CLIENT.getAtUriFromUrl(url);
    String expectedAtUri = "at://did:plc:g7c7qgmpmyysvrhuvyqi34pf/app.bsky.feed.post/3lbms7p32qv2m";
    assertEquals(expectedAtUri, atUri, "AT-URI should match the expected value.");
  }

  @Test
  public void getAtUri() throws IOException {
    System.out.println(BS_CLIENT.getAtUriFromUrl("https://bsky.app/profile/redtheone.bsky.social/post/3lbat4rmiqk2h"));
  }

  @Test
  public void getLikes() throws IOException {
    GetLikesResponse likesResponse = BS_CLIENT.getLikes("https://bsky.app/profile/redtheone.bsky.social/post/3lbat4rmiqk2h");
    assertNotNull(likesResponse);
    assertFalse(likesResponse.getLikes().isEmpty());
    assertTrue(likesResponse.getLikes().size() > 15);
  }

  @Test
  public void getFollows1stPage() throws IOException {
    GetFollowsResponse followsResponse = BS_CLIENT.getFollows("redtheone.bsky.social", null);
    assertNotNull(followsResponse);
    assertFalse(followsResponse.getFollows().isEmpty());
    assertTrue(followsResponse.getFollows().size() > 40);
    assertNotNull(followsResponse.getCursor());
  }

  @Test
  public void getAllFollows() throws IOException {
    List<Actor> follows = BS_CLIENT.getFollows("redtheone.bsky.social");
    assertTrue(follows.size() > 70);
    follows.stream().map(Actor::getHandle).toList().forEach(System.out::println);
  }

  @Test
  public void getFollowers1stPage() throws IOException {
    GetFollowersResponse response = BS_CLIENT.getFollowers("redtheone.bsky.social", null);
    assertNotNull(response);
    assertFalse(response.getFollowers().isEmpty());
    assertTrue(response.getFollowers().size() > 40);
    assertNotNull(response.getCursor());
  }

  @Test
  public void getAllFollowers() throws IOException {
    List<Actor> follows = BS_CLIENT.getFollowers("redtheone.bsky.social");
    assertTrue(follows.size() > 150);
    follows.stream().map(Actor::getHandle).toList().forEach(System.out::println);
  }

}
