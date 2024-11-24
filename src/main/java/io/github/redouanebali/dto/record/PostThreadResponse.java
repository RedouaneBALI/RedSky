package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class PostThreadResponse {

  private Thread thread;

  @Data
  public static class Thread {

    @JsonProperty("$type")
    private String       type;
    private Post         post;
    private List<Thread> replies;

  }

}