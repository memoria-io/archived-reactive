package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record FriendAdded(String eventId, String aggId, String friendId) implements UserEvent {
    public User apply(User user) {
      return user.withNewFriend(friendId);
    }
  }

  record MessageSent(String eventId, String aggId, Message message) implements UserEvent {
    public User apply(User user) {
      return user.withNewMessage(message.id());
    }
  }
}
