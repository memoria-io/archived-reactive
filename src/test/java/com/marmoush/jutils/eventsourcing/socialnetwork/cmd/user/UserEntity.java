package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.User;
import com.marmoush.jutils.general.domain.entity.Entity;

public class UserEntity extends Entity<User> {

  public UserEntity(String id, User value) {
    super(id, value);
  }
}

