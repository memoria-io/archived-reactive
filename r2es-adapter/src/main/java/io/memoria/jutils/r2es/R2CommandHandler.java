package io.memoria.jutils.r2es;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.IsolationLevel;
import reactor.core.publisher.Mono;

/**
 * An SQL based commandHandler
 */
public final class R2CommandHandler<S, C extends Command> implements CommandHandler<C> {
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  private final ConnectionFactory connectionFactory;
  private final StringTransformer stringTransformer;
  private final S initialState;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;

  public R2CommandHandler(ConnectionFactory connectionFactory,
                          StringTransformer stringTransformer,
                          S initialState,
                          Evolver<S> evolver,
                          Decider<S, C> decider) {
    this.connectionFactory = connectionFactory;
    this.stringTransformer = stringTransformer;
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
      var tableName = R2Utils.toTableName(cmd.aggId().value());
      var table = new TableConnection(con, tableName, stringTransformer);

      // Evolve
      var eventsFlux = table.createTableIfNotExists().thenMany(table.query());
      var latestState = eventsFlux.reduce(initialState, evolver);

      // Apply command
      var newEvents = latestState.map(s -> decider.apply(s, cmd).get());

      // Append events
      return newEvents.flatMap(table::appendEvents)
                      .then()
                      .doOnSuccess(s -> con.commitTransaction())
                      .doOnError(s -> con.rollbackTransaction());
    });
  }
}
