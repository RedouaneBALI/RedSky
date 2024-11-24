package io.github.redouanebali.dto.lists;

import io.github.redouanebali.dto.Actor.Actor;
import io.github.redouanebali.dto.AtUri;
import java.util.List;
import lombok.Data;


@Data
public class UserList {

  private AtUri        uri;
  private String       cid;
  private String       name;
  private String       purpose;
  private int          listItemCount;
  private String       indexedAt;
  private List<String> labels;
  private ListViewer   viewer;
  private Actor        creator;
  private String       description;

  @Data
  public static class ListViewer {

    private boolean muted;
  }

}


