package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.CreateAccount;
import io.memoria.reactive.core.eventsourcing.banking.AccountCommand.Debit;
import io.vavr.collection.List;

import java.util.Random;

class DataSet {
  private DataSet() {}

  static StateId createId(int i) {
    return StateId.of("user_id_" + i);
  }

  static String createName(int i) {
    return "name_" + i;
  }

  static List<CreateAccount> createUsers(int nUsers, int balance) {
    return List.range(0, nUsers).map(i -> CreateAccount.of(createId(i), createName(i), balance));
  }

  static List<CloseAccount> randomClosure(int nUsers) {
    return shuffledUserIds(nUsers).map(CloseAccount::of);
  }

  static List<Debit> randomOutBounds(int nUsers, int maxAmount) {
    var users = shuffledUserIds(nUsers);
    var from = users.subSequence(0, nUsers / 2);
    var to = users.subSequence(nUsers / 2, nUsers);
    var amounts = List.ofAll(new Random().ints(nUsers, 1, maxAmount).boxed());
    return List.range(0, nUsers / 2).map(i -> createOutboundBalance(from.get(i), to.get(i), amounts.get(i)));
  }

  private static Debit createOutboundBalance(StateId from, StateId to, int amount) {
    return Debit.of(from, to, amount);
  }

  private static List<StateId> shuffledUserIds(int nUsers) {
    return List.range(0, nUsers).shuffle().map(DataSet::createId);
  }
}
