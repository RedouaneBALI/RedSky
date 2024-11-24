package io.github.redouanebali.dto.record;

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
}