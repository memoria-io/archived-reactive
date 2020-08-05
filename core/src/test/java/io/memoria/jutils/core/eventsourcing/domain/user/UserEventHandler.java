package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.FriendAdded;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.MessageCreated;
import io.memoria.jutils.core.eventsourcing.domain.user.UserEvent.OnlineUserCreated;
import io.memoria.jutils.core.eventsourcing.event.EventHandler;

public record UserEventHandler() implements EventHandler<User, UserEvent> {
  @Override
  public User apply(User user, UserEvent userEvent) {
    if (user instanceof OnlineUser onlineUser) {
      if (userEvent instanceof FriendAdded friendAdded)
        return friendAdded.apply(onlineUser);
      if (userEvent instanceof MessageCreated messageCreated)
        return messageCreated.apply(onlineUser);
    }
    if (userEvent instanceof OnlineUserCreated onlineUserCreated) {
      return onlineUserCreated.apply();
    }
    return user;
  }
}
