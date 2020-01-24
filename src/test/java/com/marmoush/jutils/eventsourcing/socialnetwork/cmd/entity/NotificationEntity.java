package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.entity;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.value.Notification;
import com.marmoush.jutils.general.domain.entity.Entity;

public class NotificationEntity extends Entity<Notification> {
  public NotificationEntity(String id, Notification value) {
    super(id, value);
  }
}
