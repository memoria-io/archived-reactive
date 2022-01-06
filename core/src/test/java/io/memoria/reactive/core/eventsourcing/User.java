package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public interface User extends State {
  record Account(Id id, String name, Map<Id, List<String>> inbox) implements User {
    public Account(Id id, String name) {
      this(id, name, HashMap.empty());
    }

    public Account withOutboundMessage(Id to, String message) {
      return new Account(id, name, inbox.put(to, inbox.getOrElse(to, List.empty()).append(message)));
    }
  }

  record Visitor(Id id) implements User {}
}
