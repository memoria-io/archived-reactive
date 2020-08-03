package io.memoria.jutils.core.eventsourcing.domain.user.event;

import io.memoria.jutils.core.eventsourcing.domain.user.Message;
import io.memoria.jutils.core.eventsourcing.domain.user.User;
import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event<User> {
  record FriendAdded(String userId, String friendId) implements UserEvent {
    @Override
    public User apply(User user) {
      return user.withNewFriend(friendId());
    }
  }

  record MessageCreated(String messageId, String from, String to, String body) implements UserEvent {
    @Override
    public User apply(User user) {
      return user.withNewMessage(new Message(messageId(), from(), to(), body()));
    }
  }
}
