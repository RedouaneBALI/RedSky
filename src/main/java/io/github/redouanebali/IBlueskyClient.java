package io.github.redouanebali;

import io.github.redouanebali.dto.Actor;
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
  LikesResponse getLikes(String recordUrl, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-feed-get-likes
   */
  List<Like> getAllLikes(String recordUrl) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  FollowsResponse getFollows(String actorId, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-follows
   */
  List<Actor> getAllFollows(String actorId) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  FollowersResponse getFollowers(String actorId, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-followers
   */
  List<Actor> getAllFollowers(String actorId) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  UserListsResponse getUserLists(String actorId, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-lists
   */
  List<UserList> getAllUserLists(String actorId) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  UserListResponse getUserList(String listUri, String cursor) throws IOException;

  /**
   * https://docs.bsky.app/docs/api/app-bsky-graph-get-list
   */
  List<Actor> getAllUserList(String actorId) throws IOException;

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
}
