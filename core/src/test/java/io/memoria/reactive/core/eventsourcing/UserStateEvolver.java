package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.User.Account;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserEvent.InboundMessageCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundMessageCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.state.StateEvolver;

public record UserStateEvolver() implements StateEvolver {
  @Override
  public State apply(State user, Event event) {
    return switch (user) {
      case Visitor u -> handleVisitor(u, event);
      case Account u -> handleAccount(u, event);
      default -> user;
    };
  }

  private State handleAccount(Account user, Event event) {
    return switch (event) {
      case OutboundMessageCreated e -> user.withOutboundMessage(e.to(), e.message());
      case InboundMessageCreated e -> user.withInboundMessage(e.from(), e.message());
      default -> user;
    };
  }

  private State handleVisitor(Visitor user, Event event) {
    if (event instanceof UserCreated e) {
      return new Account(e.stateId(), e.name());
    }
    return user;
  }
}
