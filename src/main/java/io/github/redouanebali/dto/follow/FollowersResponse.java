package io.github.redouanebali.dto.follow;

import io.github.redouanebali.dto.Actor;
import java.util.List;
import lombok.Data;

@Data
public class FollowersResponse {

  private Actor       subject;
  private List<Actor> followers;
  private String      cursor;
}
