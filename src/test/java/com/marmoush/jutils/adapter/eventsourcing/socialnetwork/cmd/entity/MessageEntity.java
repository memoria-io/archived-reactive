package com.marmoush.jutils.adapter.eventsourcing.socialnetwork.cmd.entity;

import com.marmoush.jutils.adapter.eventsourcing.socialnetwork.cmd.value.Message;
import com.marmoush.jutils.domain.entity.Entity;

public class MessageEntity extends Entity<Message> {
  public MessageEntity(String id, Message value) {
    super(id, value);
  }
}
