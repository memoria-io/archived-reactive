package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.msg;

import com.marmoush.jutils.eventsourcing.domain.port.eventsourcing.Event;

public class MessageEvent extends Event {

  private MessageEvent(String flowId) {
    super(flowId);
  }

  public static class MessageCreated extends MessageEvent {
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

  public static class MessageSent extends MessageEvent {
    public final String msgId;
    public final String userId;

    public MessageSent(String eventId, String msgId, String userId) {
      super(eventId);
      this.msgId = msgId;
      this.userId = userId;
    }
  }

  public static class MessageReceived extends MessageEvent {
    public final String msgId;
    public final String userId;

    public MessageReceived(String eventId, String msgId, String userId) {
      super(eventId);
      this.msgId = msgId;
      this.userId = userId;
    }
  }
}
