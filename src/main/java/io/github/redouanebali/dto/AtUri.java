package io.github.redouanebali.dto;

import lombok.Data;

@Data
public class AtUri {

  private final String did;
  private final String collection;
  private final String rkey;

  public AtUri(String uri) {
    if (!uri.startsWith("at://")) {
      throw new IllegalArgumentException("Invalid AT-URI format: " + uri);
    }
    String[] parts = uri.substring(5).split("/", 3); // Remove "at://" and split
    if (parts.length != 3) {
      throw new IllegalArgumentException("AT-URI must have exactly 3 parts: " + uri);
    }
    this.did        = parts[0];
    this.collection = parts[1];
    this.rkey       = parts[2];
  }

  @Override
  public String toString() {
    return "at://" + did + "/" + collection + "/" + rkey;
  }

}