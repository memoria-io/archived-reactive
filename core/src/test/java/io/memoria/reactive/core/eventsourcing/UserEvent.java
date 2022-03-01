package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.util.concurrent.atomic.AtomicLong;

public sealed interface UserEvent extends Event {
  AtomicLong sKeyGen = new AtomicLong(0);

  @Override
  default long sKey() {
    return sKeyGen.getAndIncrement();
  }

  record InboundMsgCreated(Id userId, Id from, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record OutboundMsgCreated(Id userId, Id to, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record OutboundSeen(Id userId, Id seenBy) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record UserCreated(Id userId, String name) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }
}
