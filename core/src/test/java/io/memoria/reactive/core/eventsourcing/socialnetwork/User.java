package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.UUID;

sealed interface User extends State {
  record Account(Id id, String name, Map<Id, List<String>> inbox, Map<Id, Boolean> isSeen, int balance)
          implements User {
    public Account(Id id, String name) {
      this(id, name, HashMap.empty(), HashMap.empty(), 0);
    }

    public Account withInboundMessage(Id from, String message) {
      return new Account(id, name, updateInbox(from, message), isSeen, balance);
    }

    public Account withMsgSeenBy(Id seenBy) {
      return new Account(id, name, inbox, isSeen.put(seenBy, true), balance);
    }

    public Account withOutboundMessage(Id to, String message) {
      return new Account(id, name, updateInbox(to, message), isSeen, balance);
    }

    private Map<Id, List<String>> updateInbox(Id id, String message) {
      return inbox.put(id, inbox.getOrElse(id, List.empty()).append(message));
    }
  }

  record ClosedAccount(Id id) implements User {}

  record Visitor(Id id) implements User {
    public Visitor() {
      this(Id.of(UUID.randomUUID()));
    }
  }
}
