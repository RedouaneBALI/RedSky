package com.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.redouanebali.dto.record.BlueskyRecord.Facet;
import java.util.List;
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

  public CreateRecordRequest(String text, String did, String parentUri, String parentCid, String rootUri, String rootCid, List<Facet> facets) {
    this.repo   = did;
    this.record = new BlueskyRecord(text, parentUri, parentCid, rootUri, rootCid, facets);
  }
}

