package com.github.redouanebali;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.redouanebali.dto.actor.Actor;
import com.github.redouanebali.dto.record.BlueskyRecord.Facet;
import com.github.redouanebali.dto.record.CreateRecordRequest;
import com.github.redouanebali.dto.record.FeatureType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

  @Test
  public void createRecordRequestSerialization() throws JsonProcessingException {

    List<Facet> facets = new ArrayList<>();

    int           byteStart = 12;
    int           byteEnd   = 22;
    String        did       = "did:plc:tavdd37id64nlh74vaclzuwp";
    Facet.Feature feature   = new Facet.Feature(FeatureType.MENTION, did);
    Facet.Index   index     = new Facet.Index(byteStart, byteEnd);
    facets.add(new Facet("app.bsky.feed.post", List.of(feature), index));

    CreateRecordRequest recordRequest = new CreateRecordRequest("Hello c'est @redthebot !",
                                                                did, "parentUri", "parentCid", "rootUri", "rootCid", facets);
    String recordRequestValue = mapper.writeValueAsString(recordRequest);
    assertFalse(recordRequestValue.contains("null"));
  }
}
