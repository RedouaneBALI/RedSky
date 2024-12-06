package com.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReasonEnum {

  LIKE("like"),
  REPOST("repost"),
  FOLLOW("follow"),
  MENTION("mention"),
  REPLY("reply"),
  QUOTE("quote"),
  STARTERPACK_JOINED("starterpack-joined");

  private String value;

  @JsonCreator
  public static ReasonEnum fromValue(String value) {
    for (ReasonEnum reason : ReasonEnum.values()) {
      if (reason.value.equalsIgnoreCase(value)) {
        return reason;
      }
    }
    throw new IllegalArgumentException("Unknown reason: " + value);
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
