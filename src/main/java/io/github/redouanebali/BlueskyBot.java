package io.github.redouanebali;

import io.github.redouanebali.library.BlueskyClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlueskyBot {

  private static final BlueskyClient client = new BlueskyClient();

  public static void main(String[] args) {
    try {
      if (System.getenv("BLUESKY_USERNAME") == null || System.getenv("BLUESKY_PASSWORD") == null) {
        throw new IllegalArgumentException("not username or password found in environnement.");
      }
      String username = System.getenv("BLUESKY_USERNAME");
      String password = System.getenv("BLUESKY_PASSWORD");
      client.login(username, password);
      // launch bot here
    } catch (Exception e) {
      LOGGER.error("error encountered", e);
    }
  }
}