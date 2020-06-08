package io.memoria.jutils.eventsourcing.socialnetwork.domain.user;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.Objects;

public class User {
  public final String id;
  public final int age;
  public final Set<String> friends;
  public final Set<Message> messages;

  public User(String id, int age) {
    this(id, age, HashSet.empty(), HashSet.empty());
  }

  public User(String id, int age, Set<String> friends, Set<Message> messages) {
    this.id = id;
    this.age = age;
    this.friends = friends;
    this.messages = messages;
  }

  public User withNewFriend(String friendId) {
    return new User(id, age, friends.add(friendId), messages);
  }

  public User withNewMessage(Message message) {
    return new User(id, age, friends, messages.add(message));
  }

  public User withMessageSeen(String messageId, boolean seen) {
    return messages.find(m -> m.id.equals(messageId))
                   .map(msg -> messages.replace(msg, msg.withSeen(seen)))
                   .map(msgs -> new User(id, age, friends, msgs))
                   .getOrElse(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return age == user.age && id.equals(user.id) && friends.equals(user.friends) && messages.equals(user.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, age, friends, messages);
  }
}
