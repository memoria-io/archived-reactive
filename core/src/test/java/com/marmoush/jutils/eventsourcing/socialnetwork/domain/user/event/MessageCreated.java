package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event;

import java.util.Objects;

public final class MessageCreated implements UserEvent {
  public final String from;
  public final String to;
  public final String body;

  public MessageCreated(String from, String to, String body) {
    this.from = from;
    this.to = to;
    this.body = body;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MessageCreated that = (MessageCreated) o;
    return from.equals(that.from) && to.equals(that.to) && body.equals(that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, body);
  }
}
