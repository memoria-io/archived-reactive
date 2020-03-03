package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user;

import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.*;
import io.vavr.collection.List;

import java.util.Objects;

public class User {
  public final String id;
  public final int age;
  public final List<String> friends;
  public final Inbox inbox;

  public User(String id, int age) {
    this(id, age, List.empty(), new Inbox());
  }

  public User(String id, int age, List<String> friends, Inbox inbox) {
    this.id = id;
    this.age = age;
    this.friends = friends;
    this.inbox = inbox;
  }

  public User withNewFriend(String friendId) {
    return new User(this.id, this.age, this.friends.append(friendId), this.inbox);
  }

  public User withNewMessage(Message message) {
    return new User(this.id, this.age, this.friends, this.inbox.withNewMessage(message));
  }

  public User withMessageSeen(String conversationId, String messageId) {
    return new User(this.id, this.age, this.friends, this.inbox.withMessageSeen(conversationId, messageId));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return age == user.age && id.equals(user.id) && friends.equals(user.friends) && inbox.equals(user.inbox);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, age, friends, inbox);
  }
}
