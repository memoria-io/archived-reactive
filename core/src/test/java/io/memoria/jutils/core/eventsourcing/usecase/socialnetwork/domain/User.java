package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public interface User extends State {
  record Account(String id, int age, Set<String> friends, Set<String> messages) implements User {
    public Account(String id, int age) {
      this(id, age, HashSet.empty(), HashSet.empty());
    }

    public boolean canAddFriend(String friendId) {
      return !this.friends.contains(friendId);
    }

    public boolean canSendMessageTo(String friendId) {
      return this.friends.contains(friendId);
    }

    public Account withNewFriend(String friendId) {
      return new Account(id, age, friends.add(friendId), messages);
    }

    public Account withNewMessage(String messageId) {
      return new Account(id, age, friends, messages.add(messageId));
    }
  }

  record Visitor() implements User {}
}
