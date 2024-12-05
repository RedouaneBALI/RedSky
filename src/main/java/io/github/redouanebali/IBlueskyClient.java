package io.github.redouanebali;

import io.github.redouanebali.dto.actor.Actor;
import io.github.redouanebali.dto.follow.FollowersResponse;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.like.LikesResponse.Like;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListResponse;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse.Notification;
import io.github.redouanebali.dto.record.BlueskyRecord.Facet;
import io.github.redouanebali.dto.record.CreateRecordResponse;
import io.github.redouanebali.dto.record.DeleteRecordResponse;
import io.github.redouanebali.dto.record.PostThreadResponse;
import java.util.List;

public interface IBlueskyClient {

  /**
   * https://docs.bsky.app/docs/api/com-atproto-repo-create-record
   */
  Result<CreateRecordResponse> createRecord(String text);

  /**
   * https://docs.bsky.app/docs/api/com-atproto-repo-create-record
   */
  Result<CreateRecordResponse> createRecord(String text, String parentUri, String parentCid, String rootUri, String rootCid);

  /**
   * https://docs.bsky.app/docs/api/com-atproto-repo-create-record
   */
  Result<CreateRecordResponse> createRecord(String text, String parentUri, String parentCid, String rootUri, String rootCid, List<Facet> facets);

  /**
   * https://docs.bsky.app/docs/api/com-atproto-repo-delete-record
   */
  Result<DeleteRecordResponse> deleteRecord(String rkey);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-likes
   */
  Result<LikesResponse> getLikes(String recordUri, String cursor);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-likes
   */
  Result<List<Like>> getAllLikes(String recordUri);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  Result<FollowsResponse> getFollows(String actorHandle, String cursor);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  Result<List<Actor>> getAllFollows(String actorHandle);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  Result<FollowersResponse> getFollowers(String actorHandle, String cursor);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  Result<List<Actor>> getAllFollowers(String actorHandle);

  /**
   * Get the user lists of an actor. See https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  Result<UserListsResponse> getUserLists(String actorHandle, String cursor);

  /**
   * Get all the user lists of an actor. See https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  Result<List<UserList>> getAllUserLists(String actorHandle);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  Result<UserListResponse> getUserListActors(String listUri, String cursor);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  Result<List<Actor>> getAllUserListActors(String listUri);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-notification-list-notifications
   */
  Result<ListNotificationsResponse> getListNotifications(String cursor);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-notification-list-notifications
   */
  Result<List<Notification>> getAllListNotifications();

  /**
   * https://docs.bsky.app/docs/api/app-bsky-notification-list-notifications
   */
  Result<ListNotificationsResponse> getListNotifications(int limit);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-post-thread
   */
  Result<PostThreadResponse> getPostThread(String recordUri);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-actor-get-profile
   */
  Result<Actor> getProfile(String actorHandle);

  /**
   * https://docs.bsky.app/docs/api/app-bsky-actor-get-profiles
   */
  Result<List<Actor>> getProfiles(List<String> actorHandles);
}
