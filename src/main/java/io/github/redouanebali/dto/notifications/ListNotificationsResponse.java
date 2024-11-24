package io.github.redouanebali.dto.notifications;

import io.github.redouanebali.dto.Actor;
import io.github.redouanebali.dto.AtUri;
import io.github.redouanebali.dto.record.Label;
import io.github.redouanebali.dto.record.ReasonEnum;
import io.github.redouanebali.dto.record.RecordDTO;
import java.util.List;
import lombok.Data;

@Data
public class ListNotificationsResponse {

  private String             cursor;
  private List<Notification> notifications;
  private Boolean            priority;
  private String             seenAt;

  @Data
  public static class Notification {

    private AtUri       uri;
    private String      cid;
    private Actor       author;
    private ReasonEnum  reason;
    private String      reasonSubject;
    private RecordDTO   record;
    private Boolean     isRead;
    private String      indexedAt;
    private List<Label> labels;

  }
}