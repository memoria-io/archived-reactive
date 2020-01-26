package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import java.util.Objects;

public abstract class Notification {
  public final String tip;
  public final boolean seen;

  private Notification(String tip, boolean seen) {
    this.tip = tip;
    this.seen = seen;
  }

  public static final class NewMessageNotification extends Notification {
    public final Message message;

    public NewMessageNotification(boolean seen, Message message) {
      super("New message received", seen);
      this.message = message;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Notification that = (Notification) o;
    return seen == that.seen && tip.equals(that.tip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tip, seen);
  }
}
