package com.marmoush.jutils.adapter.eventsourcing.socialnetwork.cmd;

import com.marmoush.jutils.domain.port.eventsourcing.Event;

public class Events {
  private Events() {}

  public static class UserCreated implements Event {
    public final String id;
    public final String name;
    public final int age;

    public UserCreated(String id, String name, int age) {
      this.id = id;
      this.name = name;
      this.age = age;
    }
  }

  public static class MessageCreated implements Event {
    public final String id;
    public final String from;
    public final String to;
    public final String body;

    public MessageCreated(String id, String from, String to, String body) {
      this.id = id;
      this.from = from;
      this.to = to;
      this.body = body;
    }
  }

  public static class MessageSent implements Event {
    public final String messageId;
    public final String userId;

    public MessageSent(String messageId, String userId) {
      this.messageId = messageId;
      this.userId = userId;
    }
  }

  public static class MessageReceived implements Event {
    public final String messageId;
    public final String userId;

    public MessageReceived(String messageId, String userId) {
      this.messageId = messageId;
      this.userId = userId;
    }
  }
}
