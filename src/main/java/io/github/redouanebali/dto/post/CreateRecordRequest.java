package io.github.redouanebali.dto.post;

import java.time.Instant;
import lombok.Data;

@Data
public class CreateRecordRequest {

  private String    repo;
  private String    collection = "app.bsky.feed.post";
  private RecordDTO record;

  public CreateRecordRequest(String text, String did) {
    this.repo   = did;
    this.record = new RecordDTO(text);
  }

  @Data
  public static class RecordDTO {

    private final String text;
    private final String createdAt;

    public RecordDTO(String text) {
      this.text      = text;
      this.createdAt = Instant.now().toString();
    }
  }
}