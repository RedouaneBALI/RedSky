package io.github.redouanebali.dto.lists;

import java.util.List;
import lombok.Data;

@Data
public class GetUserListsResponse {

  private List<UserList> lists;
  private String         cursor;
}



