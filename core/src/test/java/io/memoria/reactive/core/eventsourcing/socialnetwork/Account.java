package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

sealed interface Account extends State {
  StateId accountId();

  default StateId stateId() {
    return accountId();
  }

  record Acc(StateId accountId,
             String name,
             Map<StateId, List<String>> inbox,
             Map<StateId, Boolean> isSeen,
             int balance) implements Account {
    public Acc(StateId accountId, String name) {
      this(accountId, name, HashMap.empty(), HashMap.empty(), 0);
    }

    public Acc withInboundMessage(StateId from, String message) {
      return new Acc(accountId, name, updateInbox(from, message), isSeen, balance);
    }

    public Acc withMsgSeenBy(StateId seenBy) {
      return new Acc(accountId, name, inbox, isSeen.put(seenBy, true), balance);
    }

    public Acc withOutboundMessage(StateId accountId, String message) {
      return new Acc(accountId, name, updateInbox(accountId, message), isSeen, balance);
    }

    private Map<StateId, List<String>> updateInbox(StateId accountId, String message) {
      return inbox.put(accountId, inbox.getOrElse(accountId, List.empty()).append(message));
    }
  }

  record ClosedAccount(StateId accountId) implements Account {}

  record Visitor(StateId accountId) implements Account {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
