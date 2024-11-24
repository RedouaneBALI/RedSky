package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.redouanebali.dto.Actor;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordDTO {

  @JsonProperty("$type")
  private String       type;
  private String       createdAt;
  @JsonInclude(Include.NON_EMPTY)
  private List<Facet>  facets;
  @JsonInclude(Include.NON_EMPTY)
  private List<String> langs;
  private String       text;
  private Actor        author;

  public RecordDTO(String text) {
    this.text      = text;
    this.createdAt = Instant.now().toString();
  }

  @Data
  public static class Facet {

    @JsonProperty("$type")
    private String        type;
    private List<Feature> features;
    private Index         index;

    @Data
    public static class Feature {

      @JsonProperty("$type")
      private String type;
      private String did;
    }

    @Data
    public static class Index {

      private Integer byteEnd;
      private Integer byteStart;
    }
  }
}