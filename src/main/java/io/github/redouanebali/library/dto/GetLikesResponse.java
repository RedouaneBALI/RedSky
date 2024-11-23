package io.github.redouanebali.library.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
public class GetLikesResponse {

  private String     uri;
  private List<Like> likes;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Like {

    private Actor  actor;
    private String createdAt;
    private String indexedAt;

  }
}