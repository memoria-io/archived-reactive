package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.msg;

import com.marmoush.jutils.general.domain.entity.Entity;

public class MessageEntity extends Entity<Message> {
  public MessageEntity(String id, Message value) {
    super(id, value);
  }
}
