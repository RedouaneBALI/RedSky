package com.github.redouanebali.dto.record;

import lombok.Data;

@Data
public class DeleteRecordResponse {

  private Commit commit;

  @Data
  public static class Commit {

    private String cid;
    private String rev;
  }
}