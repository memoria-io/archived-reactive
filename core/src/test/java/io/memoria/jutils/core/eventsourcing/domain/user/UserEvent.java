package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record OnlineUserCreated(String aggId, int age) implements UserEvent {
    public User apply() {
      return new OnlineUser(aggId, age);
    }
  }

  record FriendAdded(String aggId, String friendId) implements UserEvent {
    public User apply(OnlineUser onlineUser) {
      return onlineUser.withNewFriend(friendId);
    }
  }

  record MessageSent(String aggId, String messageId, String to, String body) implements UserEvent {
    public User apply(OnlineUser onlineUser) {
      return onlineUser.withNewMessage(new Message(messageId, aggId, to, body));
    }
  }
}
