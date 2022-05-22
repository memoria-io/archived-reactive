package io.memoria.reactive.core.eventsourcing.banking.event;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.EventId;
import io.memoria.reactive.core.eventsourcing.StateId;

public record Credited(EventId eventId, CommandId commandId, StateId creditedAcc, StateId debitedAcc, int amount)
        implements AccountEvent {
  @Override
  public StateId stateId() {
    return creditedAcc;
  }

  public static Credited of(CommandId commandId,
                            StateId creditedAcc,
                            StateId debitedAcc,
                            int amount) {
    return new Credited(EventId.randomUUID(),
                        commandId,
                        creditedAcc,
                        debitedAcc,
                        amount);
  }
}
