package io.memoria.jutils.jkafka.data.user;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.io.Serializable;

public interface User extends Serializable {
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
