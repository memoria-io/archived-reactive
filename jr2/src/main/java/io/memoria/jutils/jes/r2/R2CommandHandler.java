package io.memoria.jutils.jes.r2;

import io.memoria.jutils.jcore.eventsourcing.Command;
import io.memoria.jutils.jcore.eventsourcing.CommandHandler;
import io.memoria.jutils.jcore.eventsourcing.Decider;
import io.memoria.jutils.jcore.eventsourcing.Evolver;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.IsolationLevel;
import reactor.core.publisher.Mono;

import static io.memoria.jutils.jes.r2.SqlUtils.safeTableName;

/**
 * An SQL based commandHandler
 */
@SuppressWarnings("ClassCanBeRecord")
public final class R2CommandHandler<S, C extends Command> implements CommandHandler<C> {
  private final ConnectionFactory connectionFactory;
  private final TextTransformer textTransformer;
  private final S initialState;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;

  public R2CommandHandler(ConnectionFactory connectionFactory,
                          TextTransformer textTransformer,
                          S initialState,
                          Evolver<S> evolver,
                          Decider<S, C> decider) {
    this.connectionFactory = connectionFactory;
    this.textTransformer = textTransformer;
    this.initialState = initialState;
    this.evolver = evolver;
    this.decider = decider;
  }

  @Override
  public Mono<Void> apply(C cmd) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      // Configure Transaction
      con.setAutoCommit(true);
      con.beginTransaction();
      con.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED);

      // R2Con setup
      var tableName = safeTableName(cmd.aggId().value());
      var tableConnection = new R2Connection(con, tableName, textTransformer);

      // Evolve
      var eventsFlux = tableConnection.createTableIfNotExists().thenMany(tableConnection.query());
      var latestState = eventsFlux.reduce(initialState, evolver);

      // Apply command
      var newEvents = latestState.map(s -> decider.apply(s, cmd).get());

      // Append events
      return newEvents.flatMap(tableConnection::appendEvents)
                      .then()
                      .doOnSuccess(s -> con.commitTransaction())
                      .doOnError(s -> con.rollbackTransaction());
    });
  }
}
