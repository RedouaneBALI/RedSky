package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.redouanebali.dto.actor.Actor;
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

  public RecordDTO(String text, String parentUri, String parentCid) {
    this.text      = text;
    this.createdAt = Instant.now().toString();
    this.reply     = new Reply(parentUri, parentCid);
  }

  @Data
  public static class Facet {

    @JsonProperty("$type")
    private String        type;
    private List<Feature> features;
    private Index         index;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {

      @JsonProperty("$type")
      private String type;
      private String did;
      private String uri;
    }

    @Data
    public static class Index {

      private Integer byteEnd;
      private Integer byteStart;
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  public static class Reply {

    private Parent parent;
    private Root   root;

    public Reply(String parentUri, String parentCid) {
      this.parent = new Parent(parentUri, parentCid);
    }

    @Data
    @NoArgsConstructor
    public static class Parent {

      private String uri;
      private String cid;

      public Parent(String uri, String cid) {
        this.uri = uri;
        this.cid = cid;
      }
    }

    @Data
    public static class Root {

      private String uri;
      private String cid;
    }
  }
}

