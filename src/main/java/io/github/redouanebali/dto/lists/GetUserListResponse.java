package io.github.redouanebali.dto.lists;

import io.github.redouanebali.dto.Actor;
import java.util.List;
import lombok.Data;

@Data
public class GetUserListResponse {

  UserList       list;
  List<ListItem> items;
  String         cursor;

  @Data
  public static class ListItem {

    private String uri;
    private Actor  subject;
  }
}
