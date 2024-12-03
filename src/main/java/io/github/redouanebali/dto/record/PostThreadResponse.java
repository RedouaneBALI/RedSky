package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostThreadResponse {

  private Thread thread;
  private Thread threadgate;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Thread {

    @JsonProperty("$type")
    private String       type;
    private PostInfo     post;
    private List<Thread> replies;

  }

}