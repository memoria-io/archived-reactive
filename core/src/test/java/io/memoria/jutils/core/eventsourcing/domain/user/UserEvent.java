package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record OnlineUserCreated(String id, int age) implements UserEvent {
    public User apply() {
      return new OnlineUser(id, age);
    }
  }

  record FriendAdded(String userId, String friendId) implements UserEvent {
    public User apply(OnlineUser onlineUser) {
      return onlineUser.withNewFriend(friendId);
    }
  }

  record MessageCreated(String messageId, String from, String to, String body) implements UserEvent {
    public User apply(OnlineUser onlineUser) {
      return onlineUser.withNewMessage(new Message(messageId, from, to, body));
    }
  }
}
