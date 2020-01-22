package com.marmoush.jutils.adapter.eventsourcing.cmd.entity;

import com.marmoush.jutils.adapter.eventsourcing.cmd.value.Message;
import com.marmoush.jutils.domain.entity.Entity;

public class MessageEntity extends Entity<Message> {
  public MessageEntity(String id, Message value) {
    super(id, value);
  }
}
