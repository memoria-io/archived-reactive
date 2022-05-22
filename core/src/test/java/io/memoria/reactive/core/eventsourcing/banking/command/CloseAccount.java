package io.memoria.reactive.core.eventsourcing.banking.command;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record CloseAccount(CommandId commandId, StateId accountId) implements AccountCommand {
  public static CloseAccount of(StateId accountId) {
    return new CloseAccount(CommandId.randomUUID(), accountId);
  }
}
