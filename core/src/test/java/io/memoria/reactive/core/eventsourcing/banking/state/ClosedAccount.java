package io.memoria.reactive.core.eventsourcing.banking.state;

import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.banking.state.Account;

public record ClosedAccount(StateId accountId) implements Account {}
