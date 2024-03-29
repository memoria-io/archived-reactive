package io.memoria.reactive.eventsourcing.banking;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.banking.command.AccountCommand;
import io.memoria.reactive.eventsourcing.banking.command.CloseAccount;
import io.memoria.reactive.eventsourcing.banking.command.CreateAccount;
import io.memoria.reactive.eventsourcing.banking.command.Debit;
import io.memoria.reactive.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.eventsourcing.banking.state.Acc;
import io.memoria.reactive.eventsourcing.banking.state.Account;
import io.memoria.reactive.eventsourcing.banking.state.ClosedAccount;
import io.memoria.reactive.eventsourcing.banking.state.Visitor;
import io.memoria.reactive.eventsourcing.pipeline.Domain;
import io.memoria.reactive.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.eventsourcing.pipeline.Pipeline;
import io.memoria.reactive.eventsourcing.pipeline.Route;
import io.memoria.reactive.eventsourcing.pipeline.SagaDomain;
import io.memoria.reactive.eventsourcing.pipeline.SagaPipeline;
import io.memoria.reactive.eventsourcing.repo.Msg;
import io.memoria.reactive.eventsourcing.repo.Stream;
import io.memoria.reactive.eventsourcing.repo.mem.MemStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

class SagaPipelineTest {
  private static final Duration timeout = Duration.ofMillis(200);
  private static final TextTransformer transformer = new SerializableTransformer();

  private static final String commandTopic = "commandTopic";
  private static final String oldEventTopic = "oldEventTopic";
  private static final String newEventTopic = "newEventTopic";

  private final Route route;
  private final Stream stream;
  private final Pipeline<Account, AccountCommand, AccountEvent> pipeline;
  private final SagaPipeline<AccountEvent, AccountCommand> sagaPipeline;

  SagaPipelineTest() {
    route = new Route(commandTopic, 0, oldEventTopic, 1, newEventTopic, 1);
    stream = new MemStream(route.streamConfigs());
    pipeline = new Pipeline<>(stateDomain(), stream, transformer, route, LogConfig.FINE);
    sagaPipeline = new SagaPipeline<>(sagaDomain(), stream, transformer, route, LogConfig.FINE);
  }

  @Test
  void simple() {
    // Given
    var bobId = StateId.of("bob");
    var janId = StateId.of("jan");
    var createBob = CreateAccount.of(bobId, "bob", 100);
    var createJan = CreateAccount.of(janId, "jan", 100);
    var sendMoneyFromBobToJan = Debit.of(bobId, janId, 50);
    var requestClosure = CloseAccount.of(janId);
    // When
    Flux<Command> cmds = Flux.just(createBob, createJan, sendMoneyFromBobToJan, requestClosure, sendMoneyFromBobToJan);
    stream.publish(cmds.map(this::toMsg)).subscribe();
    // Then
    var pipelines = Flux.merge(pipeline.run(), sagaPipeline.run());
    StepVerifier.create(pipelines).expectNextCount(10).verifyTimeout(timeout);
    var bob = pipeline.stateOrInit(bobId);
    var jan = pipeline.stateOrInit(janId);
    Assertions.assertInstanceOf(Acc.class, bob);
    Assertions.assertInstanceOf(ClosedAccount.class, jan);
  }

  @Test
  void complex() {
    // Given
    int nAccounts = 4;
    int balance = 100;
    int treasury = nAccounts * balance;
    var createAccounts = DataSet.createAccounts(nAccounts, balance);
    var accountIds = createAccounts.map(AccountCommand::accountId);
    var randomOutbounds = DataSet.randomOutBounds(nAccounts, balance);
    var cmds = Flux.<Command>fromIterable(createAccounts)
                   .concatWith(Flux.fromIterable(randomOutbounds))
                   .map(this::toMsg);

    // When
    var pipelines = Flux.merge(stream.publish(cmds), pipeline.run(), sagaPipeline.run());
    StepVerifier.create(pipelines).expectNextCount(20).verifyTimeout(timeout);
    // Then
    var accounts = accountIds.map(pipeline::stateOrInit).map(u -> (Acc) u);
    Assertions.assertEquals(nAccounts, accounts.size());
    var total = accounts.foldLeft(0, (a, b) -> a + b.balance());
    Assertions.assertEquals(treasury, total);
  }

  private SagaDomain<AccountEvent, AccountCommand> sagaDomain() {
    return new SagaDomain<>(AccountEvent.class, AccountCommand.class, new AccountSaga());
  }

  private Domain<Account, AccountCommand, AccountEvent> stateDomain() {
    return new Domain<>(Account.class,
                        AccountCommand.class,
                        AccountEvent.class,
                        new Visitor(),
                        new AccountDecider(),
                        new AccountEvolver(),
                        new AccountReducer());
  }

  private Msg toMsg(Command command) {
    var body = transformer.serialize(command).get();
    return new Msg(route.commandTopic(), route.partition(), Id.of(UUID.randomUUID()), body);
  }
}
