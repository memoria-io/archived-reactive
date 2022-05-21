package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

sealed interface AccountCommand extends Command {
  @Override
  default long timestamp() {
    return 0;
  }

  record ChangeName(CommandId commandId, StateId userId, String newName) implements AccountCommand {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static ChangeName of(StateId userId, String newName) {
      return new ChangeName(CommandId.randomUUID(), userId, newName);
    }
  }

  record CreatePerson(CommandId commandId, StateId userId, String username) implements AccountCommand {
    @Override
    public StateId stateId() {
      return userId;
    }

    public static CreatePerson of(StateId userId, String username) {
      return new CreatePerson(CommandId.randomUUID(), userId, username);
    }
  }

}
