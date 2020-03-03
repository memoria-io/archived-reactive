package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event;

import java.util.Objects;

public class MessageSeen {
  public final String conversationId;
  public final String messageId;

  public MessageSeen(String conversationId, String messageId) {
    this.conversationId = conversationId;
    this.messageId = messageId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MessageSeen that = (MessageSeen) o;
    return conversationId.equals(that.conversationId) && messageId.equals(that.messageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conversationId, messageId);
  }
}
