package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox;

public class InboxMessage {

  public final String from;
  public final String to;
  public final String body;
  public final boolean seen;

  public InboxMessage(String from, String to, String body, boolean seen) {
    this.from = from;
    this.to = to;
    this.body = body;
    this.seen = seen;
  }
}
