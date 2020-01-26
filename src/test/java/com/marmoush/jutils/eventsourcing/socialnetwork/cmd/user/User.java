package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.Inbox;
import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.Message;
import io.vavr.collection.List;

import java.util.Objects;

public class User {
  public final String name;
  public final int age;
  public final List<String> friends;
  public final Inbox inbox;

  public User(String name, int age) {
    this(name, age, List.empty(), new Inbox());
  }

  public User(String name, int age, List<String> friends, Inbox inbox) {
    this.name = name;
    this.age = age;
    this.friends = friends;
    this.inbox = inbox;
  }

  public User withNewFriend(String friendId) {
    return new User(this.name, this.age, this.friends.append(friendId), this.inbox);
  }

  public User withNewMessage(Message message) {
    return new User(this.name, this.age, this.friends, this.inbox.withNewMessage(message));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return age == user.age && name.equals(user.name) && friends.equals(user.friends) && inbox.equals(user.inbox);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age, friends, inbox);
  }
}
