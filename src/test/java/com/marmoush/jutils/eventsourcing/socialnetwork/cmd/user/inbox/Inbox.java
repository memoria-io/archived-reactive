package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox;

import io.vavr.collection.List;

public class Inbox {
  public final List<InboxMessage> messages;
  public final List<InboxNotification> notifications;

  public Inbox() {
    this(List.empty(), List.empty());
  }

  public Inbox(List<InboxMessage> messages, List<InboxNotification> notifications) {
    this.messages = messages;
    this.notifications = notifications;
  }
}
