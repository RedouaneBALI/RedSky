package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FeatureType {
  MENTION("app.bsky.richtext.facet#mention"),
  LINK("app.bsky.richtext.facet#link"),
  TAG("app.bsky.richtext.facet#tag");

  private final String type;

  @JsonCreator
  public static FeatureType fromString(String value) {
    for (FeatureType featureType : FeatureType.values()) {
      if (featureType.type.equals(value)) {
        return featureType;
      }
    }
    throw new IllegalArgumentException("Unknown enum value: " + value);
  }

  @JsonValue
  @Override
  public String toString() {
    return this.type;
  }

}

