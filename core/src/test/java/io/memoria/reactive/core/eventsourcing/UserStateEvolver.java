package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.eventsourcing.User.Account;
import io.memoria.reactive.core.eventsourcing.User.Visitor;
import io.memoria.reactive.core.eventsourcing.UserEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.UserEvent.OutboundSeen;
import io.memoria.reactive.core.eventsourcing.UserEvent.UserCreated;
import io.memoria.reactive.core.eventsourcing.state.StateEvolver;

public record UserStateEvolver() implements StateEvolver {
  @Override
  public State apply(State state, Event event) {
    if (state instanceof User user && event instanceof UserEvent userEvent) {
      return apply(user, userEvent);
    }
    return state;
  }

  private State apply(User user, UserEvent userEvent) {
    return switch (userEvent) {
      case UserCreated e && user instanceof Visitor -> new Account(e.stateId(), e.name());
      case OutboundMsgCreated e && user instanceof Account acc -> acc.withOutboundMessage(e.to(), e.message());
      case InboundMsgCreated e && user instanceof Account acc -> acc.withInboundMessage(e.from(), e.message());
      case OutboundSeen e && user instanceof Account acc -> acc.withMsgSeenBy(e.seenBy());
      default -> user;
    };
  }
}
