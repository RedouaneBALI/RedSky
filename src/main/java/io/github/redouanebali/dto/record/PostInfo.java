package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.redouanebali.dto.AtUri;
import io.github.redouanebali.dto.actor.Actor;
import io.github.redouanebali.dto.actor.Actor.Viewer;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostInfo {

  private AtUri         uri;
  private String        cid;
  private Actor         author;
  private BlueskyRecord record;
  private int           replyCount;
  private int           repostCount;
  private int           likeCount;
  private int           quoteCount;
  private String        indexedAt;
  private Viewer        viewer;
  private List<Label>   labels;
}