package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.CommandId;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.AccountCommand.CreateUser;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;

class DataSet {

  private DataSet() {}

  List<CreateUser> createUserCommands(int nUsers) {
    return List.range(0, nUsers).map(i -> new CreateUser(CommandId.randomUUID(), createId(i), createName(i)));
  }

  CreateOutboundMsg sendMsg(StateId from, StateId to) {
    return new CreateOutboundMsg(CommandId.randomUUID(), from, to, createMsg(from, to));
  }

  private StateId createId(Integer i) {
    return StateId.of("user_id_" + i);
  }

  private String createName(Integer i) {
    return "name_" + i;
  }

  private String createMsg(Id from, Id to) {
    return "hello from %s to %s".formatted(from.value(), to.value());
  }
}
