package io.github.redouanebali;

import io.github.redouanebali.library.BlueskyClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlueskyBot {

  private static final BlueskyClient agent = new BlueskyClient();

  public static void main(String[] args) throws Exception {
    try {
      String username = System.getenv("BLUESKY_USERNAME");
      String password = System.getenv("BLUESKY_PASSWORD");
      agent.login(username, password);
      agent.createRecord("Premier post via API =D");
      log.info("Post publié avec succès!");
    } catch (Exception e) {
      log.error("Erreur lors de la publication", e);
      System.out.println(e.getMessage());
      System.out.println(e);
    }
  }
}