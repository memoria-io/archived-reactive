package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event;

import java.util.Objects;

public final class UserCreated implements UserEvent {
  public final String userName;
  public final int age;

  public UserCreated(String userName, int age) {
    this.userName = userName;
    this.age = age;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserCreated that = (UserCreated) o;
    return age == that.age && userName.equals(that.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, age);
  }
}




