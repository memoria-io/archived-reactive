package io.memoria.jutils.core.eventsourcing.domain.user.event;

import io.memoria.jutils.core.eventsourcing.User;
import io.memoria.jutils.core.eventsourcing.domain.user.Message;
import io.memoria.jutils.core.eventsourcing.domain.user.OnlineUser;
import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event<User> {
  record FriendAdded(String userId, String friendId) implements UserEvent {
    @Override
    public User apply(User user) {
      if (user instanceof OnlineUser onlineUser)
        return onlineUser.withNewFriend(friendId());
      return user;
    }
  }

  record MessageCreated(String messageId, String from, String to, String body) implements UserEvent {
    @Override
    public User apply(User user) {
      if (user instanceof OnlineUser onlineUser)
        return onlineUser.withNewMessage(new Message(messageId(), from(), to(), body()));
      return user;
    }
  }
}
