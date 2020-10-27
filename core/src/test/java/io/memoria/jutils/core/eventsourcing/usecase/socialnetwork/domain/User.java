package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.state.State;
import io.memoria.jutils.core.value.Id;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public interface User extends State {
  record Account(Id id, int age, Set<Id> friends, Set<Id> messages) implements User {
    public Account(Id id, int age) {
      this(id, age, HashSet.empty(), HashSet.empty());
    }

    public boolean canAddFriend(Id friendId) {
      return !this.friends.contains(friendId);
    }

    public boolean canSendMessageTo(Id friendId) {
      return this.friends.contains(friendId);
    }

    public Account withNewFriend(Id friendId) {
      return new Account(id, age, friends.add(friendId), messages);
    }

    public Account withNewMessage(Id messageId) {
      return new Account(id, age, friends, messages.add(messageId));
    }
  }

  record Visitor() implements User {}
}
