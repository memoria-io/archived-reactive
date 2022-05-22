package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.eventsourcing.banking.command.AccountCommand;
import io.memoria.reactive.core.eventsourcing.banking.command.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.command.CreateAccount;
import io.memoria.reactive.core.eventsourcing.banking.command.Debit;
import io.memoria.reactive.core.eventsourcing.banking.event.AccountEvent;
import io.memoria.reactive.core.eventsourcing.banking.state.Acc;
import io.memoria.reactive.core.eventsourcing.banking.state.Account;
import io.memoria.reactive.core.eventsourcing.banking.state.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.state.Visitor;
import io.memoria.reactive.core.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.core.eventsourcing.pipeline.Route;
import io.memoria.reactive.core.eventsourcing.pipeline.saga.SagaDomain;
import io.memoria.reactive.core.eventsourcing.pipeline.saga.SagaPipeline;
import io.memoria.reactive.core.eventsourcing.pipeline.state.StateDomain;
import io.memoria.reactive.core.eventsourcing.pipeline.state.StatePipeline;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.stream.mem.MemStream;
import io.memoria.reactive.core.stream.mem.StreamConfig;
import io.memoria.reactive.core.text.SerializableTransformer;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.collection.List;
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
  private static final String eventTopic = "eventTopic";

  private final Route route;
  private final Stream stream;
  private final StatePipeline<Account, AccountEvent, AccountCommand> statePipeline;
  private final SagaPipeline<AccountEvent, AccountCommand> sagaPipeline;

  SagaPipelineTest() {
    // Configs
    route = new Route(oldEventTopic, 0, commandTopic, eventTopic, 0, 1);
    // Infra
    var cmdTp = new StreamConfig(route.commandTopic(), route.totalPartitions(), Integer.MAX_VALUE);
    var eventTp = new StreamConfig(route.eventTopic(), route.totalPartitions(), Integer.MAX_VALUE);
    stream = new MemStream(List.of(cmdTp, eventTp).toJavaList());
    // Pipeline

    statePipeline = new StatePipeline<>(stateDomain(), stream, transformer, route, LogConfig.FINE);
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
    var pipelines = Flux.merge(statePipeline.run(), sagaPipeline.run());
    StepVerifier.create(pipelines).expectNextCount(10).verifyTimeout(timeout);
    var bob = statePipeline.stateOrInit(bobId);
    var jan = statePipeline.stateOrInit(janId);
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
    var accountIds = createAccounts.map(CreateAccount::accountId);
    var randomOutbounds = DataSet.randomOutBounds(nAccounts, balance);
    var cmds = Flux.<Command>fromIterable(createAccounts)
                   .concatWith(Flux.fromIterable(randomOutbounds))
                   .map(this::toMsg);

    // When
    var pipelines = Flux.merge(stream.publish(cmds), statePipeline.run(), sagaPipeline.run());
    StepVerifier.create(pipelines).expectNextCount(20).verifyTimeout(timeout);
    // Then
    var accounts = accountIds.map(statePipeline::stateOrInit).map(u -> (Acc) u);
    Assertions.assertEquals(nAccounts, accounts.size());
    var total = accounts.foldLeft(0, (a, b) -> a + b.balance());
    Assertions.assertEquals(treasury, total);
  }

  private SagaDomain<AccountEvent, AccountCommand> sagaDomain() {
    return new SagaDomain<>(AccountEvent.class, AccountCommand.class, new AccountSagaDecider());
  }

  private StateDomain<Account, AccountEvent, AccountCommand> stateDomain() {
    return new StateDomain<>(Account.class,
                             AccountEvent.class,
                             AccountCommand.class,
                             new Visitor(),
                             new AccountStateDecider(),
                             new AccountStateEvolver(),
                             new AccountStateReducer());
  }

  private Msg toMsg(Command command) {
    var body = transformer.blockingSerialize(command).get();
    return new Msg(route.commandTopic(), route.partition(), Id.of(UUID.randomUUID()), body);
  }
}
