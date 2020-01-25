package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

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
  }

  public static final class FriendAdded extends UserEvent {
    public final String userId;
    public final String friendId;

    public FriendAdded(String eventId, String userId, String friendId) {
      super(eventId);
      this.userId = userId;
      this.friendId = friendId;
    }
  }

  public static final class MessageCreated extends UserEvent {
    public final String msgId;
    public final String from;
    public final String to;
    public final String body;

    public MessageCreated(String flowId, String msgId, String from, String to, String body) {
      super(flowId);
      this.msgId = msgId;
      this.from = from;
      this.to = to;
      this.body = body;
    }
  }

  public static class MessageSeen extends UserEvent {
    public final String msgId;

    private MessageSeen(String flowId, String msgId) {
      super(flowId);
      this.msgId = msgId;
    }
  }
}
