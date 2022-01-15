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

  record CreateOutboundMessage(Id userId, Id to, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }

  record CreateInboundMessage(Id userId, Id from, String message) implements UserCommand {
    @Override
    public Id stateId() {
      return userId;
    }
  }
}
