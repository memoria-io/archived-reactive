package io.memoria.reactive.core.eventsourcing.user;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.Evolver;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.user.User.Account;
import io.memoria.reactive.core.eventsourcing.user.User.Visitor;
import io.memoria.reactive.core.eventsourcing.user.UserEvent.MessageSent;

public record UserEvolver() implements Evolver {
  @Override
  public State apply(State user, Event event) {
    if (user instanceof Visitor && event instanceof UserEvent.UserCreated e) {
      return new Account(e.name());
    }
    if (user instanceof Account acc && event instanceof MessageSent e) {
      return acc.withMessage(e.receiverId(), e.message());
    }
    return user;
  }
}
