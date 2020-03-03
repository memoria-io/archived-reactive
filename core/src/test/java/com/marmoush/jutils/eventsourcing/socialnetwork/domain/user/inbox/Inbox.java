package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox;

import io.vavr.collection.HashMap;

import java.util.Objects;

public class Inbox {
  public final HashMap<String, Conversation> conversations;

  public Inbox() {
    this(HashMap.empty());
  }

  public Inbox(HashMap<String, Conversation> conversations) {
    this.conversations = conversations;
  }

  public Inbox withNewMessage(Message message) {
    var conversation = conversations.getOrElse(message.from, new Conversation().withNewMessage(message));
    return new Inbox(this.conversations.put(message.from, conversation));
  }

  public Inbox withMessageSeen(String conversationId, String messageId) {
    var conversation = conversations.get(conversationId).get().withMessageSeen(messageId);
    return new Inbox(this.conversations.put(conversationId, conversation));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Inbox inbox = (Inbox) o;
    return conversations.equals(inbox.conversations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conversations);
  }
}
