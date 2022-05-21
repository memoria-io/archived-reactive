package io.memoria.reactive.core.eventsourcing.sharding;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.sharding.AccountCommand.ChangeName;
import io.memoria.reactive.core.eventsourcing.sharding.AccountCommand.CreatePerson;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;

class DataSet {
  private DataSet() {}

  public static Flux<Command> personScenario(int nAccounts, int nameChanges) {
    var createAccounts = createAccounts(nAccounts);
    var changes = List.range(0, nameChanges).flatMap(i -> changeNames(nAccounts));
    return Flux.fromIterable(createAccounts.appendAll(changes));
  }

  static StateId createId(int i) {
    return StateId.of("accountId:" + i);
  }

  static String createName(int i) {
    return "name:" + i;
  }

  static List<Command> createAccounts(int nAccounts) {
    return List.range(0, nAccounts).map(i -> CreatePerson.of(createId(i), createName(i)));
  }

  static List<Command> changeNames(int nAccounts) {
    return List.range(0, nAccounts).map(i -> ChangeName.of(createId(i), createName(i)));
  }
}
