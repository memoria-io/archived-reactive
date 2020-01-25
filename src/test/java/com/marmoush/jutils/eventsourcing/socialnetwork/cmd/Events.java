package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

public class Events {
  private Events() {}

  public static class UserEvent extends Event {

    public UserEvent(String flowId) {
      super(flowId);
    }
  }

  public static class UserCreated extends UserEvent {
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

  public static class FriendAdded extends UserEvent {
    public final String userId;
    public final String friendId;

    public FriendAdded(String eventId, String userId, String friendId) {
      super(eventId);
      this.userId = userId;
      this.friendId = friendId;
    }
  }

  public static class MessageCreated extends UserEvent {
    public final String msgId;
    public final String from;
    public final String to;
    public final String body;

    public MessageCreated(String eventId, String msgId, String from, String to, String body) {
      super(eventId);
      this.msgId = msgId;
      this.from = from;
      this.to = to;
      this.body = body;
    }
  }

  public static class MessageSent extends UserEvent {
    public final String msgId;
    public final String userId;

    public MessageSent(String eventId, String msgId, String userId) {
      super(eventId);
      this.msgId = msgId;
      this.userId = userId;
    }
  }

  public static class MessageReceived extends UserEvent {
    public final String msgId;
    public final String userId;

    public MessageReceived(String eventId, String msgId, String userId) {
      super(eventId);
      this.msgId = msgId;
      this.userId = userId;
    }
  }
}
