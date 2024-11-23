package io.github.redouanebali.library.dto;

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