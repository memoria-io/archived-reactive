package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.pipeline.StateEvolver;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Acc;
import io.memoria.reactive.core.eventsourcing.socialnetwork.Account.Visitor;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.AccountCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.InboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.OutboundMsgCreated;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountEvent.OutboundSeen;

record AccountStateEvolver() implements StateEvolver {
  @Override
  public State apply(State state, Event event) {
    if (state instanceof Account account && event instanceof AccountEvent accountEvent) {
      return apply(account, accountEvent);
    }
    return state;
  }

  private State apply(Account account, AccountEvent accountEvent) {
    return switch (accountEvent) {
      case AccountCreated e && account instanceof Visitor -> new Acc(e.stateId(), e.name());
      case OutboundMsgCreated e && account instanceof Acc acc -> acc.withOutboundMessage(e.msgReceiver(), e.message());
      case InboundMsgCreated e && account instanceof Acc acc -> acc.withInboundMessage(e.msgSender(), e.message());
      case OutboundSeen e && account instanceof Acc acc -> acc.withMsgSeenBy(e.msgReceiver());

      default -> account;
    };
  }
}
