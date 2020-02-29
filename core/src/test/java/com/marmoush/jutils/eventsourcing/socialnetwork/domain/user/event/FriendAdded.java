package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event;

import java.util.Objects;

public final class FriendAdded implements UserEvent {
  public final String userId;
  public final String friendId;

  public FriendAdded(String userId, String friendId) {
    this.userId = userId;
    this.friendId = friendId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    FriendAdded that = (FriendAdded) o;
    return userId.equals(that.userId) && friendId.equals(that.friendId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, friendId);
  }
}