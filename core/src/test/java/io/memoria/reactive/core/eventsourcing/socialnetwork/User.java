package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

sealed interface User extends State {
  record Account(StateId id, String name, Map<StateId, List<String>> inbox, Map<StateId, Boolean> isSeen, int balance)
          implements User {
    public Account(StateId id, String name) {
      this(id, name, HashMap.empty(), HashMap.empty(), 0);
    }

    public Account withInboundMessage(StateId from, String message) {
      return new Account(id, name, updateInbox(from, message), isSeen, balance);
    }

    public Account withMsgSeenBy(StateId seenBy) {
      return new Account(id, name, inbox, isSeen.put(seenBy, true), balance);
    }

    public Account withOutboundMessage(StateId to, String message) {
      return new Account(id, name, updateInbox(to, message), isSeen, balance);
    }

    private Map<StateId, List<String>> updateInbox(StateId id, String message) {
      return inbox.put(id, inbox.getOrElse(id, List.empty()).append(message));
    }
  }

  record ClosedAccount(StateId id) implements User {}

  record Visitor(StateId id) implements User {
    public Visitor() {
      this(StateId.randomUUID());
    }
  }
}
