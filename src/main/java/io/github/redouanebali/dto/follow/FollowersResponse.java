package io.github.redouanebali.dto.follow;

import io.github.redouanebali.dto.Paginated;
import io.github.redouanebali.dto.actor.Actor;
import java.util.List;
import lombok.Data;

@Data
public class FollowersResponse implements Paginated {

  private Actor       subject;
  private List<Actor> followers;
  private String      cursor;

  @Override
  public List retrieveItems() {
    return followers;
  }
}
