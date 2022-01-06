package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.User.Account;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageSent;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;

public record UserEvolver() implements Evolver {
  @Override
  public State apply(State user, Event event) {
    if (user instanceof Visitor && event instanceof UserCreated e) {
      return new Account(e.stateId(), e.name());
    }
    if (user instanceof Account acc && event instanceof MessageSent e) {
      return acc.withOutboundMessage(e.to(), e.message());
    }
    return user;
  }
}
