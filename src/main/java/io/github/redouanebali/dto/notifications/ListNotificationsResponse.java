package io.github.redouanebali.dto.notifications;

import io.github.redouanebali.dto.AtUri;
import io.github.redouanebali.dto.Paginated;
import io.github.redouanebali.dto.actor.Actor;
import io.github.redouanebali.dto.record.BlueskyRecord;
import io.github.redouanebali.dto.record.Label;
import io.github.redouanebali.dto.record.ReasonEnum;
import java.util.List;
import lombok.Data;

@Data
public class ListNotificationsResponse implements Paginated {

  private String             cursor;
  private List<Notification> notifications;
  private Boolean            priority;
  private String             seenAt;

  @Override
  public List retrieveItems() {
    return notifications;
  }

  @Data
  public static class Notification {

    private AtUri         uri;
    private String        cid;
    private Actor         author;
    private ReasonEnum    reason;
    private String        reasonSubject;
    private BlueskyRecord record;
    private Boolean       isRead;
    private String        indexedAt;
    private List<Label>   labels;

  }
}