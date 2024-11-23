package io.github.redouanebali.library.dto;

import java.util.List;
import lombok.Data;

@Data
public class GetFollowersResponse {

  private Actor       subject;
  private List<Actor> followers;
  private String      cursor;
}
