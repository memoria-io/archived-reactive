package io.memoria.reactive.eventsourcing.banking.command;

import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.StateId;

public record CloseAccount(CommandId commandId, StateId accountId) implements AccountCommand {
  public static CloseAccount of(StateId accountId) {
    return new CloseAccount(CommandId.randomUUID(), accountId);
  }
}
