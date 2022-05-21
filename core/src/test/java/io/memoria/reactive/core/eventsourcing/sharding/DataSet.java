package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.sharding.AccountCommand.ChangeName;
import io.memoria.reactive.core.eventsourcing.sharding.AccountCommand.CreatePerson;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;

class DataSet {
  private DataSet() {}

  public static Flux<Command> personScenario(int nUsers, int nameChanges) {
    var createUsers = createPersons(nUsers);
    var changes = List.range(0, nameChanges).flatMap(i -> changeNames(nUsers));
    return Flux.fromIterable(createUsers.appendAll(changes));
  }

  static StateId createId(int i) {
    return StateId.of("accountId:" + i);
  }

  static String createName(int i) {
    return "name:" + i;
  }

  static List<Command> createPersons(int nUsers) {
    return List.range(0, nUsers).map(i -> CreatePerson.of(createId(i), createName(i)));
  }

  static List<Command> changeNames(int nUsers) {
    return List.range(0, nUsers).map(i -> ChangeName.of(createId(i), createName(i)));
  }
}
