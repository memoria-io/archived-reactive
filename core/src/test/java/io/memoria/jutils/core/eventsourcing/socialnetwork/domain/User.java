package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Entity;
import io.memoria.jutils.core.value.Id;

public class User extends Entity<UserValue> {
  public User(Id id, UserValue value) {
    super(id, value);
  }
}
