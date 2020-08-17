package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public record User(String id, int age, Set<String> friends, Set<String> messages) implements State {
  public User(String id, int age) {
    this(id, age, HashSet.empty(), HashSet.empty());
  }

  public boolean canAddFriend(String friendId) {
    return !this.friends.contains(friendId);
  }

  public boolean canSendMessageTo(String friendId) {
    return this.friends.contains(friendId);
  }

  public User withNewFriend(String friendId) {
    return new User(id, age, friends.add(friendId), messages);
  }

  public User withNewMessage(String messageId) {
    return new User(id, age, friends, messages.add(messageId));
  }
}
