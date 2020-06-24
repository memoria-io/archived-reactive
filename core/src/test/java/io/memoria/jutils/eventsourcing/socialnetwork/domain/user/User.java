package io.memoria.jutils.eventsourcing.socialnetwork.domain.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public record User(String id, int age, Set<String>friends, Set<Message>messages) {
  public User(String id, int age) {
    this(id, age, HashSet.empty(), HashSet.empty());
  }

  public User withMessageSeen(String messageId, boolean seen) {
    return messages.find(m -> m.id().equals(messageId))
                   .map(msg -> messages.replace(msg, msg.withSeen(seen)))
                   .map(msgs -> new User(id, age, friends, msgs))
                   .getOrElse(this);
  }

  public User withNewFriend(String friendId) {
    return new User(id, age, friends.add(friendId), messages);
  }

  public User withNewMessage(Message message) {
    return new User(id, age, friends, messages.add(message));
  }
}
