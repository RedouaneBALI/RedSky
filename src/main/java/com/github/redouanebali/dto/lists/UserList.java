package com.github.redouanebali.dto.lists;

import com.github.redouanebali.dto.AtUri;
import com.github.redouanebali.dto.actor.Actor;
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


