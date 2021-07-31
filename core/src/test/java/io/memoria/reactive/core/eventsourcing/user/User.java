package io.memoria.reactive.core.eventsourcing.user;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public interface User extends State {
  record Account(String name, Map<Id, List<String>> inbox) implements User {
    public Account(String name) {
      this(name, HashMap.empty());
    }

    public Account withMessage(Id id, String message) {
      return new Account(name, inbox.put(id, inbox.getOrElse(id, List.empty()).append(message)));
    }
  }

  record Visitor() implements User {}
}
