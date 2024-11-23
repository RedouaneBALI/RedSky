package io.github.redouanebali.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actor {

  private String      did;
  private String      handle;
  private String      displayName;
  private String      avatar;
  private Associated  associated;
  private Viewer      viewer;
  private List<Label> labels;
  private String      createdAt;
  private String      description;
  private String      indexedAt;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Associated {

    private Chat chat;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Chat {

      private String allowIncoming;
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Viewer {

    private boolean muted;
    private boolean blockedBy;
    private AtUri   following;
    private AtUri   followedBy;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Label {

    private String id;
    private String value;
  }
}
