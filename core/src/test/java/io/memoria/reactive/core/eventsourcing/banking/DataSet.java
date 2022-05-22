package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.banking.command.AccountCommand;
import io.memoria.reactive.core.eventsourcing.banking.command.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.command.CreateAccount;
import io.memoria.reactive.core.eventsourcing.banking.command.Debit;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;

import java.util.Random;

class DataSet {
  private DataSet() {}

  static Flux<AccountCommand> scenario(int nAccounts, int nameChanges) {
    var createAccounts = createAccounts(nAccounts, 0);
    var changes = List.range(0, nameChanges).flatMap(i -> randomOutBounds(nAccounts, 100));
    return Flux.fromIterable(createAccounts.appendAll(changes));
  }

  static StateId createId(int i) {
    return StateId.of("acc_id_" + i);
  }

  static String createName(int i) {
    return "name_" + i;
  }

  static List<AccountCommand> createAccounts(int nAccounts, int balance) {
    return List.range(0, nAccounts).map(i -> CreateAccount.of(createId(i), createName(i), balance));
  }

  static List<AccountCommand> randomClosure(int nAccounts) {
    return shuffledIds(nAccounts).map(CloseAccount::of);
  }

  static List<AccountCommand> randomOutBounds(int nAccounts, int maxAmount) {
    var accounts = shuffledIds(nAccounts);
    var from = accounts.subSequence(0, nAccounts / 2);
    var to = accounts.subSequence(nAccounts / 2, nAccounts);
    var amounts = List.ofAll(new Random().ints(nAccounts, 1, maxAmount).boxed());
    return List.range(0, nAccounts / 2).map(i -> createOutboundBalance(from.get(i), to.get(i), amounts.get(i)));
  }

  private static AccountCommand createOutboundBalance(StateId from, StateId to, int amount) {
    return Debit.of(from, to, amount);
  }

  private static List<StateId> shuffledIds(int nAccounts) {
    return List.range(0, nAccounts).shuffle().map(DataSet::createId);
  }
}
