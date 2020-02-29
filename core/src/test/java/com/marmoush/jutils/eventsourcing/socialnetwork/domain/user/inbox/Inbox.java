package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import io.vavr.collection.List;

import java.util.Objects;

public class Inbox {
  public final List<Message> messages;
  public final List<Notification> notifications;

  public Inbox() {
    this(List.empty(), List.empty());
  }

  public Inbox(List<Message> messages, List<Notification> notifications) {
    this.messages = messages;
    this.notifications = notifications;
  }

  public Inbox withNewMessage(Message message) {
    return new Inbox(this.messages.append(message),
                     this.notifications.append(new NewMessageNotification(false, message)));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Inbox inbox = (Inbox) o;
    return messages.equals(inbox.messages) && notifications.equals(inbox.notifications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages, notifications);
  }
}
