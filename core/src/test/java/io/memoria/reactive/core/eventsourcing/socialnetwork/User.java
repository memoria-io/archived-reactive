package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

sealed interface User extends State {
  record Account(StateId stateId, String name, Map<StateId, List<String>> inbox, Map<StateId, Boolean> isSeen, int balance)
          implements User {
    public Account(StateId stateId ,String name) {
      this(stateId, name, HashMap.empty(), HashMap.empty(), 0);
    }

    public Account withInboundMessage(StateId from, String message) {
      return new Account(stateId, name, updateInbox(from, message), isSeen, balance);
    }

    public Account withMsgSeenBy(StateId seenBy) {
      return new Account(stateId, name, inbox, isSeen.put(seenBy, true), balance);
    }

    public Account withOutboundMessage(StateId stateId, String message) {
      return new Account(stateId, name, updateInbox(stateId, message), isSeen, balance);
    }

    private Map<StateId, List<String>> updateInbox(StateId stateId ,String message) {
      return inbox.put(stateId, inbox.getOrElse(stateId, List.empty()).append(message));
    }
  }

  record ClosedAccount(StateId stateId) implements User {}

  record Visitor(StateId stateId) implements User {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
