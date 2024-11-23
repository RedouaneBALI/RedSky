package io.github.redouanebali;

import io.github.redouanebali.dto.Actor;
import io.github.redouanebali.dto.follow.GetFollowersResponse;
import io.github.redouanebali.dto.follow.GetFollowsResponse;
import io.github.redouanebali.dto.like.GetLikesResponse;
import io.github.redouanebali.dto.like.GetLikesResponse.Like;
import io.github.redouanebali.dto.lists.GetUserListResponse;
import io.github.redouanebali.dto.lists.GetUserListsResponse;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.post.CreateRecordResponse;
import io.github.redouanebali.dto.post.DeleteRecordResponse;
import java.io.IOException;
import java.util.List;

public interface IBlueskyClient {

  /**
   * https://docs.bsky.app/docs/api/com-atproto-repo-create-record
   */
  CreateRecordResponse createRecord(String text) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/com-atproto-repo-delete-record
   */
  DeleteRecordResponse deleteRecord(String rkey) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-likes
   */
  GetLikesResponse getLikes(String recordUrl, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-likes
   */
  List<Like> getLikes(String recordUrl) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  GetFollowsResponse getFollows(String actorId, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  List<Actor> getFollows(String actorId) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  GetFollowersResponse getFollowers(String actorId, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  List<Actor> getFollowers(String actorId) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  GetUserListsResponse getUserLists(String actorId, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  List<UserList> getUserLists(String actorId) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  GetUserListResponse getUserList(String listUri, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  List<Actor> getUserList(String actorId) throws IOException;

}
