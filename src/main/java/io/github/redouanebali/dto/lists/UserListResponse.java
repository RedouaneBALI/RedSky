package io.github.redouanebali.dto.lists;

import io.github.redouanebali.dto.Paginated;
import io.github.redouanebali.dto.actor.Actor;
import java.util.List;
import lombok.Data;

@Data
public class UserListResponse implements Paginated<UserListResponse.ListItem> {

  private UserList       list;
  private List<ListItem> items;
  private String         cursor;

  @Override
  public String getCursor() {
    return cursor;
  }

  @Override
  public List<ListItem> retrieveItems() {
    return items;
  }


  @Data
  public static class ListItem {

    private String uri;
    private Actor  subject;
  }
}
