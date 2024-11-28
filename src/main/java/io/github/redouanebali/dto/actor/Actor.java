package io.github.redouanebali.dto.actor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.redouanebali.dto.record.RecordDTO;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actor {

  private String               did;
  private String               handle;
  private String               displayName;
  private String               description;
  private String               avatar;
  private String               banner;
  private int                  followersCount;
  private int                  followsCount;
  private int                  postsCount;
  private Associated           associated;
  private JoinedViaStarterPack joinedViaStarterPack;
  private String               indexedAt;
  private String               createdAt;
  private Viewer               viewer;
  private List<Label>          labels;
  private PinnedPost           pinnedPost;

  @Data
  public static class Associated {

    private int     lists;
    private int     feedgens;
    private int     starterPacks;
    private boolean labeler;
    private Chat    chat;

    @Data
    public static class Chat {

      private String allowIncoming;
    }
  }

  @Data
  public static class JoinedViaStarterPack {

    private String      uri;
    private String      cid;
    private RecordDTO   record;
    private Creator     creator;
    private int         listItemCount;
    private int         joinedWeekCount;
    private int         joinedAllTimeCount;
    private List<Label> labels;
    private String      indexedAt;

    @Data
    public static class Creator {

      private String      did;
      private String      handle;
      private String      displayName;
      private String      avatar;
      private Associated  associated;
      private Viewer      viewer;
      private List<Label> labels;
      private String      createdAt;
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Viewer {

    private boolean        muted;
    private MutedByList    mutedByList;
    private boolean        blockedBy;
    private String         blocking;
    private BlockingByList blockingByList;
    private String         following;
    private String         followedBy;
    private KnownFollowers knownFollowers;

    @Data
    public static class MutedByList {

      private String      uri;
      private String      cid;
      private String      name;
      private String      purpose;
      private String      avatar;
      private int         listItemCount;
      private List<Label> labels;
      private Viewer      viewer;
      private String      indexedAt;
    }

    @Data
    public static class BlockingByList {

      private String      uri;
      private String      cid;
      private String      name;
      private String      purpose;
      private String      avatar;
      private int         listItemCount;
      private List<Label> labels;
      private Viewer      viewer;
      private String      indexedAt;
    }

    @Data
    public static class KnownFollowers {

      private int         count;
      private List<Actor> followers;

    }
  }

  @Data
  public static class Label {

    private int     ver;
    private String  src;
    private String  uri;
    private String  cid;
    private String  val;
    private boolean neg;
    private String  cts;
    private String  exp;
    private String  sig;
  }

  @Data
  public static class PinnedPost {

    private String uri;
    private String cid;
  }

}