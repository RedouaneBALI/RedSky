package io.github.redouanebali.dto.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.redouanebali.dto.actor.Actor;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlueskyRecord {

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

  public BlueskyRecord(String text) {
    this.text      = text;
    this.createdAt = Instant.now().toString();
  }

  public BlueskyRecord(String text, String parentUri, String parentCid, String rootUri, String rootCid) {
    this.text      = text;
    this.createdAt = Instant.now().toString();
    this.reply     = new Reply(parentUri, parentCid, rootUri, rootCid);
  }

  public BlueskyRecord(String text, String parentUri, String parentCid, String rootUri, String rootCid, List<Facet> facets) {
    this.text      = text;
    this.createdAt = Instant.now().toString();
    this.reply     = new Reply(parentUri, parentCid, rootUri, rootCid);
    this.facets    = facets;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Facet {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("$type")
    private String        type;
    private List<Feature> features;
    private Index         index;

    public Facet(List<Feature> features) {
      this.type     = "app.bsky.feed.post";
      this.features = features;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Feature {

      @JsonInclude(JsonInclude.Include.NON_NULL)
      @JsonProperty("$type")
      private FeatureType type;
      private String      did;
      private String      uri;

      public Feature(FeatureType type, String did) {
        this.type = type;
        this.did  = did;
      }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Index {

      private Integer byteStart;
      private Integer byteEnd;
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  public static class Reply {

    private Parent parent;
    private Root   root;

    public Reply(String parentUri, String parentCid, String rootUri, String rootCid) {
      this.parent = new Parent(parentUri, parentCid);
      this.root   = new Root(rootUri, rootCid);
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parent {

      @JsonProperty("$type")
      private String type;
      private String uri;
      private String cid;

      public Parent(String uri, String cid) {
        this.uri = uri;
        this.cid = cid;
      }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root {

      @JsonProperty("$type")
      private String type;
      private String uri;
      private String cid;

      public Root(String uri, String cid) {
        this.uri = uri;
        this.cid = cid;
      }
    }
  }
}

