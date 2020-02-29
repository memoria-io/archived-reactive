package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user;

import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.*;
import io.vavr.collection.List;

import java.util.Objects;

public class User {
  public final String userName;
  public final int age;
  public final List<String> friends;
  public final Inbox inbox;

  public User(String userName, int age) {
    this(userName, age, List.empty(), new Inbox());
  }

  public User(String userName, int age, List<String> friends, Inbox inbox) {
    this.userName = userName;
    this.age = age;
    this.friends = friends;
    this.inbox = inbox;
  }

  public User withNewFriend(String friendId) {
    return new User(this.userName, this.age, this.friends.append(friendId), this.inbox);
  }

  public User withNewMessage(Message message) {
    return new User(this.userName, this.age, this.friends, this.inbox.withNewMessage(message));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return age == user.age && userName.equals(user.userName) && friends.equals(user.friends) && inbox.equals(user.inbox);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, age, friends, inbox);
  }
}
