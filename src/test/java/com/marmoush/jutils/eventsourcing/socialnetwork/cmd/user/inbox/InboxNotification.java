package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox;

public class InboxNotification {
  public final String tip;
  public final boolean seen;

  public InboxNotification(String tip, boolean seen) {
    this.tip = tip;
    this.seen = seen;
  }

  public static final class NewMessageNotification extends InboxNotification {
    public final String messageId;

    public NewMessageNotification(String tip, boolean seen, String messageId) {
      super("New message received", seen);
      this.messageId = messageId;
    }
  }
}
