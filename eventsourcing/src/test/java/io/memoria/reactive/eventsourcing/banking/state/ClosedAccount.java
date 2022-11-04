package io.memoria.reactive.eventsourcing.banking.state;

import io.memoria.reactive.eventsourcing.StateId;

public record ClosedAccount(StateId accountId) implements Account {}
