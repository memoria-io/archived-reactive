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

    public MessageCreated(String flowId, String msgId, String from, String to, String body) {
      super(flowId);
      this.msgId = msgId;
      this.from = from;
      this.to = to;
      this.body = body;
    }
  }

  public static class MessageSeen extends MessageEvent{
    public final String msgId;
    private MessageSeen(String flowId, String msgId) {
      super(flowId);
      this.msgId = msgId;
    }
  }
}
