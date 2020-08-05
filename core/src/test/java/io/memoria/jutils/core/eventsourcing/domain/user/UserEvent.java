package io.memoria.jutils.core.eventsourcing.domain.user;

import io.memoria.jutils.core.eventsourcing.event.Event;

public interface UserEvent extends Event {
  record OnlineUserCreated(String userId, int age) implements UserEvent {
    public User apply() {
      return new OnlineUser(userId, age);
    }
  }

  record FriendAdded(String userId, String friendId) implements UserEvent {
    public User apply(OnlineUser onlineUser) {
      return onlineUser.withNewFriend(friendId);
    }
  }

  record MessageReceived(String userId, String messageId, String from, String body) implements UserEvent {
    public User apply(OnlineUser onlineUser) {
      return onlineUser.withNewMessage(new Message(messageId, from, userId, body));
    }
  }

  String userId();

  @Override
  default String aggId() {
    return userId();
  }
}
