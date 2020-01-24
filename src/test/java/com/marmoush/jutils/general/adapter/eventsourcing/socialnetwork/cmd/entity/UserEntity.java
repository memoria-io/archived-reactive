package com.marmoush.jutils.general.adapter.eventsourcing.socialnetwork.cmd.entity;

import com.marmoush.jutils.general.adapter.eventsourcing.socialnetwork.cmd.value.User;
import com.marmoush.jutils.general.domain.entity.Entity;

public class UserEntity extends Entity<User> {

  public UserEntity(String id, User value) {
    super(id, value);
  }
}

