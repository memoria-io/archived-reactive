package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

public record Message(String id, String from, String to, String body, boolean seen) {
  public Message(String id, String from, String to, String body) {
    this(id, from, to, body, false);
  }

  public Message withSeen(boolean isSeen) {
    return new Message(id, from, to, body, isSeen);
  }
}
