package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import java.util.Objects;

public final class NewMessageNotification extends Notification {
  public final Message message;

  public NewMessageNotification(boolean seen, Message message) {
    super("New message received", seen);
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    NewMessageNotification that = (NewMessageNotification) o;
    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), message);
  }
}
