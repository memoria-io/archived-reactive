package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

sealed interface Account extends State {
  record Acc(StateId stateId, String name, Map<StateId, List<String>> inbox, Map<StateId, Boolean> isSeen, int balance)
          implements Account {
    public Acc(StateId stateId, String name) {
      this(stateId, name, HashMap.empty(), HashMap.empty(), 0);
    }

    public Acc withInboundMessage(StateId from, String message) {
      return new Acc(stateId, name, updateInbox(from, message), isSeen, balance);
    }

    public Acc withMsgSeenBy(StateId seenBy) {
      return new Acc(stateId, name, inbox, isSeen.put(seenBy, true), balance);
    }

    public Acc withOutboundMessage(StateId stateId, String message) {
      return new Acc(stateId, name, updateInbox(stateId, message), isSeen, balance);
    }

    private Map<StateId, List<String>> updateInbox(StateId stateId, String message) {
      return inbox.put(stateId, inbox.getOrElse(stateId, List.empty()).append(message));
    }
  }

  record ClosedAccount(StateId stateId) implements Account {}

  record Visitor(StateId stateId) implements Account {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
