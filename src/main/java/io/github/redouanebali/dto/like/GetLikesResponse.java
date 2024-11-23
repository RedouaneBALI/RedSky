package io.github.redouanebali.dto.like;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.redouanebali.dto.Actor;
import java.util.List;
import lombok.Data;

@Data
public class GetLikesResponse {

  private String     uri;
  private List<Like> likes;
  private String     cursor;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Like {

    private Actor  actor;
    private String createdAt;
    private String indexedAt;

  }
}