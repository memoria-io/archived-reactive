package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.value;

public class Notification {
  public final String message;

  private Notification(String message) {
    this.message = message;
  }

  public static Notification messageReceivedNote(String messageId) {
    return new Notification("Message was received with id:" + messageId);
  }

  public static Notification messageSentNote(String messageId) {
    return new Notification("Message with id:" + messageId + " was sent");
  }
}
