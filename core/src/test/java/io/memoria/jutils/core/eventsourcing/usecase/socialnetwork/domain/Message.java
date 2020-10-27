package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain;

import io.memoria.jutils.core.value.Id;

public record Message(Id id, Id from, Id to, String body, boolean seen) {
  public Message(Id id, Id from, Id to, String body) {
    this(id, from, to, body, false);
  }

  public Message withSeen(boolean isSeen) {
    return new Message(id, from, to, body, isSeen);
  }
}
