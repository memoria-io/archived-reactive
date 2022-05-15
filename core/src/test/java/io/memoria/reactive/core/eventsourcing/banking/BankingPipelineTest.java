package io.memoria.reactive.core.eventsourcing.banking;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.banking.User.Account;
import io.memoria.reactive.core.eventsourcing.banking.User.ClosedAccount;
import io.memoria.reactive.core.eventsourcing.banking.User.Visitor;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.CloseAccount;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.CreateUser;
import io.memoria.reactive.core.eventsourcing.banking.UserCommand.Debit;
import io.memoria.reactive.core.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.core.eventsourcing.pipeline.Route;
import io.memoria.reactive.core.eventsourcing.pipeline.SagaPipeline;
import io.memoria.reactive.core.eventsourcing.pipeline.StatePipeline;
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

class BankingPipelineTest {
  private static final Duration timeout = Duration.ofMillis(200);
  private static final TextTransformer transformer = new SerializableTransformer();

  private static final String commandTopic = "commandTopic";
  private static final String oldEventTopic = "oldEventTopic";
  private static final String eventTopic = "eventTopic";

  private final Route route;
  private final Stream stream;
  private final StatePipeline statePipeline;
  private final SagaPipeline sagaPipeline;

  BankingPipelineTest() {
    // Configs
    route = new Route(oldEventTopic, 0, commandTopic, eventTopic, 0, 1);
    // Infra
    var cmdTp = new StreamConfig(route.commandTopic(), route.totalPartitions(), Integer.MAX_VALUE);
    var eventTp = new StreamConfig(route.eventTopic(), route.totalPartitions(), Integer.MAX_VALUE);
    stream = new MemStream(List.of(cmdTp, eventTp).toJavaList());
    // Pipeline
    statePipeline = new StatePipeline(stream,
                                      transformer,
                                      new Visitor(),
                                      new UserStateDecider(),
                                      new UserStateEvolver(),
                                      route,
                                      LogConfig.FINE);
    sagaPipeline = new SagaPipeline(stream, transformer, new UserSagaDecider(), route, LogConfig.FINE);
  }

  @Test
  void simple() {
    // Given
    Id bobId = Id.of("bob");
    var createUserBob = CreateUser.of(bobId, "bob", 100);
    Id janId = Id.of("jan");
    var createUserJan = CreateUser.of(janId, "jan", 100);
    var sendMoneyFromBobToJan = Debit.of(bobId, janId, 50);
    var requestClosure = CloseAccount.of(janId);
    // When
    Flux<Command> cmds = Flux.just(createUserBob,
                                   createUserJan,
                                   sendMoneyFromBobToJan,
                                   requestClosure,
                                   sendMoneyFromBobToJan);
    stream.publish(cmds.map(this::toMsg)).subscribe();
    // Then
    var pipelines = Flux.merge(statePipeline.run(), sagaPipeline.run());
    StepVerifier.create(pipelines).expectNextCount(10).verifyTimeout(timeout);
    var bob = statePipeline.stateOrInit(bobId);
    var jan = statePipeline.stateOrInit(janId);
    Assertions.assertInstanceOf(Account.class, bob);
    Assertions.assertInstanceOf(ClosedAccount.class, jan);
  }

  @Test
  void complex() {
    // Given
    int nUsers = 4;
    int balance = 100;
    int treasury = nUsers * balance;
    var createUsers = DataSet.createUsers(nUsers, balance);
    var userIds = createUsers.map(CreateUser::userId);
    var randomOutbounds = DataSet.randomOutBounds(nUsers, balance);
    var cmds = Flux.<Command>fromIterable(createUsers).concatWith(Flux.fromIterable(randomOutbounds)).map(this::toMsg);

    // When
    var pipelines = Flux.merge(stream.publish(cmds), statePipeline.run(), sagaPipeline.run());
    StepVerifier.create(pipelines).expectNextCount(20).verifyTimeout(timeout);
    // Then
    var users = userIds.map(statePipeline::stateOrInit).map(u -> (Account) u);
    Assertions.assertEquals(nUsers, users.size());
    var total = users.foldLeft(0, (a, b) -> a + b.balance());
    Assertions.assertEquals(treasury, total);
  }

  private Msg toMsg(Command command) {
    var body = transformer.blockingSerialize(command).get();
    return new Msg(route.commandTopic(), route.partition(), Id.of(UUID.randomUUID()), body);
  }
}
