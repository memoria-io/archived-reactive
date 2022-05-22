package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record AccountCreated(EventId eventId, CommandId commandId, StateId accountId, String name, int balance)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return accountId;
  }

  public static AccountCreated of(CommandId commandId,
                                  StateId accountId,
                                  String name,
                                  int balance) {
    return new AccountCreated(EventId.randomUUID(),
                              commandId,
                              accountId,
                              name,
                              balance);
  }
}
