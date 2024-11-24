package io.github.redouanebali.dto.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteRecordRequest {

  private String collection = "app.bsky.feed.post";
  private String repo;
  private String rkey;

  public DeleteRecordRequest(String repo, String rkey) {
    this.repo = repo;
    this.rkey = rkey;
  }
}
