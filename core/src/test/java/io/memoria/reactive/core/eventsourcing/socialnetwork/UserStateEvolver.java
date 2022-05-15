package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;
import io.memoria.reactive.core.eventsourcing.socialnetwork.User.Account;
import io.memoria.reactive.core.eventsourcing.socialnetwork.User.Visitor;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserEvent.OutboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserEvent.OutboundSeen;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserEvent.UserCreated;

record UserStateEvolver() implements StateEvolver {
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
      case OutboundMsgCreated e && user instanceof Account acc -> acc.withOutboundMessage(e.msgReceiver(), e.message());
      case InboundMsgCreated e && user instanceof Account acc -> acc.withInboundMessage(e.msgSender(), e.message());
      case OutboundSeen e && user instanceof Account acc -> acc.withMsgSeenBy(e.msgReceiver());

      default -> user;
    };
  }
}
