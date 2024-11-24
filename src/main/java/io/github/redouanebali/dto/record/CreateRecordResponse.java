package io.github.redouanebali.dto.record;

import io.github.redouanebali.dto.AtUri;
import lombok.Data;

@Data
public class CreateRecordResponse {

  private AtUri  uri;
  private String cid;
  private Commit commit;
  private String validationStatus;

  @Data
  public static class Commit {

    private String cid;
    private String rev;
  }
}