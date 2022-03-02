package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.UUID;

public sealed interface User extends State {
  record Account(Id id, String name, Map<Id, List<String>> inbox, Map<Id, Boolean> isSeen) implements User {
    public Account(Id id, String name) {
      this(id, name, HashMap.empty(), HashMap.empty());
    }

    public Account withInboundMessage(Id from, String message) {
      return new Account(id, name, updateInbox(from, message), isSeen);
    }

    public Account withMsgSeenBy(Id seenBy) {
      return new Account(id, name, inbox, isSeen.put(seenBy, true));
    }

    public Account withOutboundMessage(Id to, String message) {
      return new Account(id, name, updateInbox(to, message), isSeen);
    }

    private Map<Id, List<String>> updateInbox(Id id, String message) {
      return inbox.put(id, inbox.getOrElse(id, List.empty()).append(message));
    }
  }

  record Visitor(Id id) implements User {
    public Visitor() {
      this(Id.of(UUID.randomUUID()));
    }
  }
}
