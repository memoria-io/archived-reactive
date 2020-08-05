package io.memoria.jutils.core.eventsourcing.domain.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public record OnlineUser(String id, int age, Set<String> friends, Set<Message> messages) implements User {
  public OnlineUser(String id, int age) {
    this(id, age, HashSet.empty(), HashSet.empty());
  }

  public boolean canAddFriend(String friendId) {
    return !this.friends.contains(friendId);
  }

  public boolean canSendMessageTo(String friendId) {
    return this.friends.contains(friendId);
  }

  public OnlineUser withNewFriend(String friendId) {
    return new OnlineUser(id, age, friends.add(friendId), messages);
  }

  public OnlineUser withNewMessage(Message message) {
    return new OnlineUser(id, age, friends, messages.add(message));
  }
}
