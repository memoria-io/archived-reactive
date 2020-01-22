package com.marmoush.jutils.adapter.eventsourcing.cmd.entity;

import com.marmoush.jutils.adapter.eventsourcing.cmd.value.User;
import com.marmoush.jutils.domain.entity.Entity;

public class UserEntity extends Entity<User> {

  public UserEntity(String id, User value) {
    super(id, value);
  }
}

