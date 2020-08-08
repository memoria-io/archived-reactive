package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageSent;
import io.memoria.jutils.core.eventsourcing.event.EventHandler;

public record UserEventHandler() implements EventHandler<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent userEvent) {
    if (userEvent instanceof FriendAdded friendAdded)
      return friendAdded.apply(user);
    if (userEvent instanceof MessageSent messageSent)
      return messageSent.apply(user);
    return user;
  }
}
