package com.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostThreadResponse {

  private Thread     thread;
  private Threadgate threadgate;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Thread {

    @JsonProperty("$type")
    private String       type;
    private PostInfo     post;
    private List<Thread> replies;

  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Threadgate {

    private String        uri;
    private String        cid;
    private BlueskyRecord record;
    private List<String>  lists;
  }
}