package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.notification;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.notification.Notification;
import com.marmoush.jutils.general.domain.entity.Entity;

public class NotificationEntity extends Entity<Notification> {
  public NotificationEntity(String id, Notification value) {
    super(id, value);
  }
}
