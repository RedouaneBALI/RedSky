package io.github.redouanebali.dto.like;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.redouanebali.dto.Paginated;
import io.github.redouanebali.dto.actor.Actor;
import java.util.List;
import lombok.Data;

@Data
public class LikesResponse implements Paginated {

  private String     uri;
  private List<Like> likes;
  private String     cursor;

  @Override
  public List retrieveItems() {
    return likes;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Like {

    private Actor  actor;
    private String createdAt;
    private String indexedAt;

  }
}