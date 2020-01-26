package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

import java.util.Objects;

public abstract class UserEvent extends Event {

  private UserEvent(String flowId) {
    super(flowId);
  }

  public static final class UserCreated extends UserEvent {
    public final String userId;
    public final String name;
    public final int age;

    public UserCreated(String eventId, String userId, String name, int age) {
      super(eventId);
      this.userId = userId;
      this.name = name;
      this.age = age;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      UserCreated that = (UserCreated) o;
      return age == that.age && userId.equals(that.userId) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, name, age);
    }
  }

  public static final class FriendAdded extends UserEvent {
    public final String userId;
    public final String friendId;

    public FriendAdded(String eventId, String userId, String friendId) {
      super(eventId);
      this.userId = userId;
      this.friendId = friendId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      FriendAdded that = (FriendAdded) o;
      return userId.equals(that.userId) && friendId.equals(that.friendId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(userId, friendId);
    }
  }

  public static final class MessageCreated extends UserEvent {
    public final String from;
    public final String to;
    public final String body;

    public MessageCreated(String flowId, String from, String to, String body) {
      super(flowId);
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

  public static class MessageSeen extends UserEvent {
    public final String msgId;

    private MessageSeen(String flowId, String msgId) {
      super(flowId);
      this.msgId = msgId;
    }

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
}
