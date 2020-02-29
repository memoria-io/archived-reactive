package com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.event;

import java.util.Objects;

public class MessageSeen implements UserEvent {
  public final String msgId;

  private MessageSeen(String msgId) {
    this.msgId = msgId;
  }

  //    @Override
  //    public User apply(User user) {
  //      return null;
  //    }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MessageSeen that = (MessageSeen) o;
    return msgId.equals(that.msgId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(msgId);
  }
}