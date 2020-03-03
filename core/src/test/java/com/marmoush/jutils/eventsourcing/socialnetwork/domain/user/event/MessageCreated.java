package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event;

import java.util.Objects;

public final class MessageCreated implements UserEvent {
  public final String messageId;
  public final String from;
  public final String to;
  public final String body;

  public MessageCreated(String messageId, String from, String to, String body) {
    this.messageId = messageId;
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
    return messageId.equals(that.messageId) && from.equals(that.from) && to.equals(that.to) && body.equals(that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageId, from, to, body);
  }
}
