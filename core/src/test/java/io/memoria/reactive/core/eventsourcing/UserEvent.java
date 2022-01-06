package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.id.IdGenerator;
import io.memoria.reactive.core.id.SerialIdGenerator;

public sealed interface UserEvent extends Event {
  // For simplicity
  IdGenerator gen = new SerialIdGenerator();
  
  @Override
  default Id id() {
    return gen.get();
  }

  record MessageSent(Id from, Id to, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return from;
    }
  }

  record MessageReceived(Id from, Id to, String message) implements UserEvent {
    @Override
    public Id stateId() {
      return to;
    }
  }

  record NotificationReceived(Id userId) implements UserEvent {
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
