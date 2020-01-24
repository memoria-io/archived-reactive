package com.marmoush.jutils.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

public class Events {
  private Events() {}

  public static class UserCreated extends Event {
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

  public static class MessageCreated extends Event {
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

  public static class MessageSent extends Event {
    public final String msgId;
    public final String userId;

    public MessageSent(String eventId, String msgId, String userId) {
      super(eventId);
      this.msgId = msgId;
      this.userId = userId;
    }
  }

  public static class MessageReceived extends Event {
    public final String msgId;
    public final String userId;

    public MessageReceived(String eventId, String msgId, String userId) {
      super(eventId);
      this.msgId = msgId;
      this.userId = userId;
    }
  }
}
