package io.github.redouanebali;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.redouanebali.dto.Actor.Actor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class SerializationTest {

  private static final BlueskyClient BS_CLIENT = new BlueskyClient();
  ObjectMapper mapper = new ObjectMapper();

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
  public void writeFollowersIntoJsonFile() throws IOException {
    List<String> actors = List.of("redtheone.bsky.social");
    for (String actor : actors) {
      List<Actor>  followers = BS_CLIENT.getAllFollowers(actor).getValue();
      List<String> dids      = followers.stream().map(Actor::getDid).toList();
      String       filePath  = "src/test/resources/followers/" + actor + ".json";
      mapper.writeValue(new File(filePath), dids);
    }
  }
}
