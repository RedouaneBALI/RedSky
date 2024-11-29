package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateRecordRequest {

  private String    repo;
  private String    collection = "app.bsky.feed.post";
  private RecordDTO record;

  public CreateRecordRequest(String text, String did) {
    this.repo   = did;
    this.record = new RecordDTO(text);
  }

  public CreateRecordRequest(String text, String did, String parentUri, String parentCid) {
    this.repo = did;
    if (parentUri != null && parentCid != null) {
      this.record = new RecordDTO(text, parentUri, parentCid);
    } else {
      this.record = new RecordDTO(text);
    }
  }
}

