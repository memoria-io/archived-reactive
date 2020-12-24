package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.eventsourcing.Entity;
import io.memoria.jutils.core.value.Id;

public class UserEntity extends Entity<User> {
  public UserEntity(Id id, User user) {
    super(id, user);
  }
}
