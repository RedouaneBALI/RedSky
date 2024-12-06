package com.github.redouanebali.dto.lists;

import com.github.redouanebali.dto.Paginated;
import java.util.List;
import lombok.Data;

@Data
public class UserListsResponse implements Paginated {

  private List<UserList> lists;
  private String         cursor;

  @Override
  public List retrieveItems() {
    return lists;
  }
}



