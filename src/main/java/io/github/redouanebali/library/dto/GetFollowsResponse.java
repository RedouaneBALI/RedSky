package io.github.redouanebali.library.dto;

import java.util.List;
import lombok.Data;

@Data
public class GetFollowsResponse {

  private Actor       subject;
  private List<Actor> follows;
  private String      cursor;
}
