package io.memoria.jutils.eventsourcing.socialnetwork.domain.user.cmd;

import java.util.Objects;

public final class AddFriend implements UserCommand {
  public final String userId;
  public final String friendId;

  public AddFriend(String userId, String friendId) {

    this.userId = userId;
    this.friendId = friendId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    AddFriend addFriend = (AddFriend) o;
    return userId.equals(addFriend.userId) && friendId.equals(addFriend.friendId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), userId, friendId);
  }
}
