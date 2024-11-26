package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class PostThreadResponse {

  private Thread thread;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Thread {

    @JsonProperty("$type")
    private String       type;
    private PostInfo     post;
    private List<Thread> replies;

  }

}