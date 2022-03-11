package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.util.UUID;

public sealed interface UserEvent extends Event {

  @Override
  default Id id() {
    return Id.of(UUID.randomUUID());
  }

  @Override
  default long timestamp() {
    return 0;
  }

  record InboundMsgCreated(Id commandId, Id userId, Id from, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record OutboundMsgCreated(Id commandId, Id userId, Id to, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record OutboundSeen(Id commandId, Id userId, Id seenBy) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record UserCreated(Id commandId, Id userId, String name) implements UserEvent {
    @Override
    public Id stateId() {
      return userId;
    }
  }
}
