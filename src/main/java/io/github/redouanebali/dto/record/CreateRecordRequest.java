package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateRecordRequest {

  private String        repo;
  private String        collection = "app.bsky.feed.post";
  private BlueskyRecord record;

  public CreateRecordRequest(String text, String did) {
    this.repo   = did;
    this.record = new BlueskyRecord(text);
  }

  public CreateRecordRequest(String text, String did, String parentUri, String parentCid, String rootUri, String rootCid) {
    this.repo   = did;
    this.record = new BlueskyRecord(text, parentUri, parentCid, rootUri, rootCid);
  }
}

