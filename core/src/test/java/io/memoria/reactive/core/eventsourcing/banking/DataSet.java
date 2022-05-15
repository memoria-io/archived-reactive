package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.banking.UserCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.Debit;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;

import java.util.Random;

class DataSet {
  private DataSet() {}

  static Id createId(int i) {
    return Id.of("user_id_" + i);
  }

  static String createName(int i) {
    return "name_" + i;
  }

  static List<CreateUser> createUsers(int nUsers, int balance) {
    return List.range(0, nUsers).map(i -> CreateUser.of(createId(i), createName(i), balance));
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

  private static Debit createOutboundBalance(Id from, Id to, int amount) {
    return Debit.of(from, to, amount);
  }

  private static List<Id> shuffledUserIds(int nUsers) {
    return List.range(0, nUsers).shuffle().map(DataSet::createId);
  }
}
