package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

public interface UserCommand extends Command {
  @Override
  default Id id() {
    return Id.of(0);
  }

  record CreateUser(Id userId, String username) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record SendMessage(Id from, Id to, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return from;
    }
  }
}
