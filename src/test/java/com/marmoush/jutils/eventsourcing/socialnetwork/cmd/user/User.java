package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox.Inbox;
import io.vavr.collection.List;

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
}
