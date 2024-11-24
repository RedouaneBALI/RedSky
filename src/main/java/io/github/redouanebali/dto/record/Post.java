package io.github.redouanebali.dto.record;

import io.github.redouanebali.dto.Actor;
import io.github.redouanebali.dto.Actor.Viewer;
import io.github.redouanebali.dto.AtUri;
import java.util.List;
import lombok.Data;

@Data
public class Post {

  private AtUri       uri;
  private String      cid;
  private Actor       author;
  private RecordDTO   record;
  private int         replyCount;
  private int         repostCount;
  private int         likeCount;
  private int         quoteCount;
  private String      indexedAt;
  private Viewer      viewer;
  private List<Label> labels;
}