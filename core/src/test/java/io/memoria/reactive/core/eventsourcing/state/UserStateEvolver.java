package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.User.Account;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageReceived;
import io.memoria.reactive.core.eventsourcing.UserEvent.MessageSent;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;

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
      case MessageSent e -> user.withOutboundMessage(e.to(), e.message());
      case MessageReceived e -> user.withInboundMessage(e.from(), e.message());
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
