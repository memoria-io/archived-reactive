package io.memoria.jutils.eventsourcing.socialnetwork;

import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.ESException.InvalidOperation;
import io.memoria.jutils.core.id.Id;
import io.memoria.jutils.core.id.IdGenerator;
import io.memoria.jutils.eventsourcing.memory.InMemoryCommandHandler;
import io.memoria.jutils.eventsourcing.r2.R2CommandHandler;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.User;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserCommand.SendMessage;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserDecider;
import io.memoria.jutils.eventsourcing.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.eventsourcing.socialnetwork.transformer.SocialNetworkTransformer;
import io.memoria.jutils.eventsourcing.sql.SqlCommandHandler;
import io.r2dbc.spi.ConnectionFactories;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.LastStep;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

class SocialNetworkTest {
  private static final AtomicInteger atomic = new AtomicInteger();
  private static final IdGenerator idGenerator = () -> Id.of(atomic.getAndIncrement() + "");
  private final SocialNetworkTestData testData = new SocialNetworkTestData(new Random(), idGenerator);

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void failurePath(CommandHandler<UserCommand> handler) {
    // When
    var create = handler.apply(testData.create);
    // Then
    StepVerifier.create(create).expectComplete().verify();
    StepVerifier.create(create).expectError(InvalidOperation.class).verify();
  }

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void happyPath(CommandHandler<UserCommand> handler) {
    // Given
    var commands = Flux.just(testData.create, testData.add, testData.send);
    // When
    var result = commands.map(handler)
                         .map(StepVerifier::create)
                         .map(LastStep::expectComplete)
                         .map(StepVerifier::verify);
    // Then
    StepVerifier.create(result).expectNextCount(3).expectComplete().verify();
  }

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void manyCommands(CommandHandler<UserCommand> handler) {
    // Given
    var commands = Flux.just(testData.create, testData.add, testData.send);
    // When
    var init = commands.map(handler).map(StepVerifier::create).map(LastStep::expectComplete).map(StepVerifier::verify);
    // Then
    StepVerifier.create(init).expectNextCount(3).expectComplete().verify();

    // Given
    var sendFlux = Flux.range(0, 100)
                       .map(i -> new SendMessage(Id.of("cmd_" + i), testData.userId, testData.friendId, "hello_" + i));
    // When
    var result = sendFlux.map(handler)
                         .map(StepVerifier::create)
                         .map(LastStep::expectComplete)
                         .map(StepVerifier::verify);
    StepVerifier.create(result).expectNextCount(100).expectComplete().verify();
  }

  @ParameterizedTest
  @MethodSource("commandHandlers")
  void oneCommand(CommandHandler<UserCommand> handler) {
    // When
    var eventFlux = handler.apply(testData.create);
    // Then
    StepVerifier.create(eventFlux).expectComplete().verify();
  }

  private static Stream<Arguments> commandHandlers() throws SQLException {
    return Stream.of(of(memCH()), of(r2CH()), of(sqlCH()));
  }

  private static CommandHandler<UserCommand> memCH() {
    var db = new ConcurrentHashMap<Id, User>();
    return new InMemoryCommandHandler<>(db, new Visitor(), new UserEvolver(), new UserDecider(idGenerator));
  }

  private static CommandHandler<UserCommand> r2CH() {
    var connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///testR2");
    return new R2CommandHandler<>(connectionFactory,
                                  new SocialNetworkTransformer(),
                                  new Visitor(),
                                  new UserEvolver(),
                                  new UserDecider(idGenerator));
  }

  private static CommandHandler<UserCommand> sqlCH() throws SQLException {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:///testJDBC");
    ds.setUser("sa");
    ds.setPassword("sa");
    var pc = ds.getPooledConnection();
    return new SqlCommandHandler<>(pc,
                                   new SocialNetworkTransformer(),
                                   new Visitor(),
                                   new UserEvolver(),
                                   new UserDecider(idGenerator),
                                   Schedulers.boundedElastic());
  }
}
