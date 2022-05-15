package io.memoria.reactive.core.eventsourcing.socialnetwork;

import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateOutboundMsg;
import io.memoria.reactive.core.eventsourcing.socialnetwork.UserCommand.CreateUser;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.id.IdGenerator;
import io.memoria.reactive.core.id.SerialIdGenerator;
import io.vavr.collection.List;

class DataSet {
  private static final IdGenerator idGenerator = new SerialIdGenerator();

  private DataSet() {}

  List<CreateUser> createUserCommands(int nUsers) {
    return List.range(0, nUsers).map(i -> new CreateUser(idGenerator.get(), createId(i), createName(i)));
  }

  CreateOutboundMsg sendMsg(Id from, Id to) {
    return new CreateOutboundMsg(idGenerator.get(), from, to, createMsg(from, to));
  }

  private Id createId(Integer i) {
    return Id.of("user_id_" + i);
  }

  private String createName(Integer i) {
    return "name_" + i;
  }

  private String createMsg(Id from, Id to) {
    return "hello from %s to %s".formatted(from.value(), to.value());
  }
}
