import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.Actor;
import io.github.redouanebali.dto.follow.GetFollowsResponse;
import io.github.redouanebali.dto.like.GetLikesResponse;
import io.github.redouanebali.dto.lists.GetUserListResponse;
import io.github.redouanebali.dto.lists.GetUserListsResponse;
import io.github.redouanebali.dto.lists.UserList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class DeserializationTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testDeserializeGetLikesResponse() throws IOException {
    Path             jsonFilePath = Path.of("src/test/resources/getLikesResponse.json");
    String           jsonContent  = Files.readString(jsonFilePath);
    GetLikesResponse response     = objectMapper.readValue(jsonContent, GetLikesResponse.class);
    assertNotNull(response, "Response object should not be null");
    assertNotNull(response.getUri(), "URI should not be null");
    assertNotNull(response.getLikes(), "Likes list should not be null");
    assertFalse(response.getLikes().isEmpty(), "Likes list should not be empty");
    assertEquals(16, response.getLikes().size());
    GetLikesResponse.Like firstLike = response.getLikes().getFirst();
    assertNotNull(firstLike.getActor(), "Actor should not be null");
    assertEquals("xouyou.bsky.social", firstLike.getActor().getHandle(), "Handle should match expected value");
  }

  @Test
  public void testDeserializeGetFollows() throws IOException {
    Path               jsonFilePath = Path.of("src/test/resources/getFollows.json");
    String             jsonContent  = Files.readString(jsonFilePath);
    GetFollowsResponse response     = objectMapper.readValue(jsonContent, GetFollowsResponse.class);
    assertNotNull(response);
    assertEquals("3kbxxx7wual2f", response.getCursor());
    assertEquals(47, response.getFollows().size());
  }

  @Test
  public void testDeserializeGetUserLists() throws IOException {
    Path                 jsonFilePath = Path.of("src/test/resources/getUserLists.json");
    String               jsonContent  = Files.readString(jsonFilePath);
    GetUserListsResponse response     = objectMapper.readValue(jsonContent, GetUserListsResponse.class);
    assertNotNull(response);
    assertEquals(2, response.getLists().size());
    UserList userList = response.getLists().getLast();
    assertNotNull(userList.getUri());
    assertTrue(userList.getListItemCount() > 0);
  }

  @Test
  public void testDeserializeGetUserList() throws IOException {
    Path                jsonFilePath = Path.of("src/test/resources/getUserList.json");
    String              jsonContent  = Files.readString(jsonFilePath);
    GetUserListResponse userList     = objectMapper.readValue(jsonContent, GetUserListResponse.class);
    assertNotNull(userList.getList());
    assertNotNull(userList.getItems());
    Actor actor = userList.getItems().getFirst().getSubject();
    assertNotNull(actor.getHandle());
  }

}
