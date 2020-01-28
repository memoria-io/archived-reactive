package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import java.util.Objects;

public class Message {
  public final String from;
  public final String to;
  public final String body;
  public final boolean seen;

  public Message(String from, String to, String body) {
    this(from, to, body, false);
  }

  public Message(String from, String to, String body, boolean seen) {
    this.from = from;
    this.to = to;
    this.body = body;
    this.seen = seen;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Message message = (Message) o;
    return seen == message.seen && from.equals(message.from) && to.equals(message.to) && body.equals(message.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, body, seen);
  }
}
