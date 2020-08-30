package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.tests;

import io.memoria.jutils.core.eventsourcing.TestingInMemoryEventStore;
import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserDecider;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.generator.IdGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerTest {
  private static final Map<String, List<UserEvent>> db = new HashMap<>();
  private static final EventStore<UserEvent> eventStore = new TestingInMemoryEventStore<>(db);
  private static final IdGenerator idGen = () -> "0";
  private static final String workSpaceAggId = "02";
  private static final CommandHandler<User, UserEvent, UserCommand> handler = new CommandHandler<>(eventStore,
                                                                                                   new UserEvolver(),
                                                                                                   new UserDecider(idGen),
                                                                                                   new Visitor());

  @Test
  public void handle() {
    var handleMono = handler.handle(workSpaceAggId, new CreateAccount(idGen.get(), 18));
    StepVerifier.create(handleMono).expectComplete().verify();
    Assertions.assertThat(db.get(workSpaceAggId)).contains(new AccountCreated("0", "0", 18));
  }
}
