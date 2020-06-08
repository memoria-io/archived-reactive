package io.memoria.jutils.eventsourcing.socialnetwork.domain.user;

import java.util.Objects;

public class Message {
  public final String id;
  public final String from;
  public final String to;
  public final String body;
  public final boolean seen;

  public Message(String id, String from, String to, String body) {
    this(id, from, to, body, false);
  }

  public Message(String id, String from, String to, String body, boolean seen) {
    this.id = id;
    this.from = from;
    this.to = to;
    this.body = body;
    this.seen = seen;
  }

  public Message withSeen(boolean isSeen) {
    return new Message(id, from, to, body, isSeen);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Message message = (Message) o;
    return seen == message.seen && id.equals(message.id) && from.equals(message.from) && to.equals(message.to) &&
           body.equals(message.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, from, to, body, seen);
  }
}
