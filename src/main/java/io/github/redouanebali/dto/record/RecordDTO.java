package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.redouanebali.dto.Actor.Actor;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
  private Reply        reply;

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

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Reply {

    private Parent parent;
    private Root   root;

    @Data
    public static class Parent {

      private String cid;
      private String uri;
    }

    @Data
    public static class Root {

      private String cid;
      private String uri;
    }
  }
}