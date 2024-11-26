package io.github.redouanebali;

import io.github.redouanebali.dto.Actor.Actor;
import io.github.redouanebali.dto.follow.FollowersResponse;
import io.github.redouanebali.dto.follow.FollowsResponse;
import io.github.redouanebali.dto.like.LikesResponse;
import io.github.redouanebali.dto.like.LikesResponse.Like;
import io.github.redouanebali.dto.lists.UserList;
import io.github.redouanebali.dto.lists.UserListResponse;
import io.github.redouanebali.dto.lists.UserListsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse;
import io.github.redouanebali.dto.notifications.ListNotificationsResponse.Notification;
import io.github.redouanebali.dto.record.CreateRecordResponse;
import io.github.redouanebali.dto.record.DeleteRecordResponse;
import io.github.redouanebali.dto.record.PostThreadResponse;
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
  LikesResponse getLikes(String recordUri, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-likes
   */
  List<Like> getAllLikes(String recordUri) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  FollowsResponse getFollows(String actorHandle, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  List<Actor> getAllFollows(String actorHandle) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  FollowersResponse getFollowers(String actorHandle, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  List<Actor> getAllFollowers(String actorHandle) throws IOException;

  /**
   * Get the user lists of an actor. See https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  UserListsResponse getUserLists(String actorHandle, String cursor) throws IOException;

  /**
   * Get all the user lists of an actor. See https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  List<UserList> getAllUserLists(String actorHandle) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  UserListResponse getUserListActors(String listUri, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  List<Actor> getAllUserListActors(String listUri) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-notification-list-notifications
   */
  ListNotificationsResponse getListNotifications(String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-notification-list-notifications
   */
  List<Notification> getAllListNotifications() throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-post-thread
   */
  PostThreadResponse getPostThread(String recordUri) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-actor-get-profile
   */
  Actor getProfile(String actorHandle) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-actor-get-profiles
   */
  List<Actor> getProfiles(List<String> actorHandles) throws IOException;
}
