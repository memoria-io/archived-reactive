package io.memoria.jutils.core.eventsourcing.domain;

import io.memoria.jutils.core.eventsourcing.domain.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.event.Evolver;

public record UserEvolver() implements Evolver<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent userEvent) {
    if (userEvent instanceof FriendAdded e)
      return apply(user, e);
    if (userEvent instanceof MessageSent e)
      return apply(user, e);
    return user;
  }

  private User apply(User user, FriendAdded e) {
    return user.withNewFriend(e.friendId());
  }

  private User apply(User user, MessageSent e) {
    return user.withNewMessage(e.message().id());
  }
}
