package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import io.vavr.collection.List;

public class User {
  public final String name;
  public final int age;
  public final List<String> friends;

  public User(String name, int age, List<String> friends) {
    this.name = name;
    this.age = age;
    this.friends = friends;
  }
}
