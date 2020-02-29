package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import java.util.Objects;

public abstract class Notification {
  public final String tip;
  public final boolean seen;

  public Notification(String tip, boolean seen) {
    this.tip = tip;
    this.seen = seen;
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
