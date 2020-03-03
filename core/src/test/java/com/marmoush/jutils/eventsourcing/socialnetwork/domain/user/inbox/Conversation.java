package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import io.vavr.collection.LinkedHashMap;

import java.util.Objects;

public class Conversation {
  public final LinkedHashMap<String, Message> messages;

  public Conversation() {
    this(LinkedHashMap.empty());
  }

  public Conversation(LinkedHashMap<String, Message> messages) {
    this.messages = messages;
  }

  public Conversation withNewMessage(Message message) {
    return new Conversation(this.messages.put(message.id, message));
  }

  public Conversation withMessageSeen(String messageId) {
    var m = this.messages.get(messageId).get();
    return new Conversation(this.messages.put(messageId, m));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Conversation that = (Conversation) o;
    return messages.equals(that.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages);
  }
}
