package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox;

import io.vavr.collection.List;

public class UserInbox {
  public final String userId;
  public final List<InboxMessage> messages;
  public final List<InboxNotification> notifications;

  public UserInbox(String userId, List<InboxMessage> messages, List<InboxNotification> notifications) {
    this.userId = userId;
    this.messages = messages;
    this.notifications = notifications;
  }
}
