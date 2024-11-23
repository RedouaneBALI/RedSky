package io.github.redouanebali.library.dto;

import lombok.Data;

@Data
public class CreateRecordResponse {

  private String uri;
  private String cid;
  private Commit commit;
  private String validationStatus;

  public String getRkey() {
    if (uri != null && uri.contains("/")) {
      String[] parts = uri.split("/");
      return parts[parts.length - 1];
    }
    return null;
  }

  @Data
  public static class Commit {

    private String cid;
    private String rev;
  }
}