package io.memoria.jutils.r2es;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

import static io.memoria.jutils.r2es.R2Utils.executeUpdate;

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
    return Mono.from(connectionFactory.create()).flatMap(con -> apply(con, cmd));
  }

  public Mono<Void> apply(Connection con, C cmd) {
    var tableName = R2Utils.toTableName(cmd.aggId().value());
    // Configure Transaction
    con.setAutoCommit(true);
    con.beginTransaction();
    con.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED);
    // Evolve
    var eventsFlux = createTableIfNotExists(con, tableName).thenMany(query(con, tableName));
    var latestState = eventsFlux.reduce(initialState, evolver);
    // Apply command
    var newEvents = latestState.map(s -> decider.apply(s, cmd).get());
    // Append events
    return newEvents.flatMap(events -> appendEvents(con, tableName, events)).then();
  }

  public Mono<Integer> appendEvents(Connection connection, String tableName, List<Event> events) {
    var sql = "INSERT INTO %s (%s, %s) ".formatted(tableName, CREATED_AT_COL, PAYLOAD_COL) + "VALUES($1, $2)";
    var st = connection.createStatement(sql);
    for (Event e : events) {
      var eventPayload = this.stringTransformer.serialize(e).get();
      st.bind("$1", Timestamp.valueOf(e.createdAt()).toString()).bind("$2", eventPayload);
      st.add();
    }
    return Mono.<Result>from(st.execute()).flatMap(r -> Mono.from(r.getRowsUpdated()))
                                          .doOnSuccess(s -> connection.commitTransaction())
                                          .doOnError(s -> connection.rollbackTransaction());
  }

  public Mono<Integer> createTableIfNotExists(Connection connection, String tableName) {
    var sql = """
              CREATE TABLE IF NOT EXISTS %s (
              id INT GENERATED ALWAYS AS IDENTITY,
              createdAt TIMESTAMP NOT NULL,
              payload TEXT NOT NULL,
              PRIMARY KEY (id)
            )
            """.formatted(tableName);
    return executeUpdate(connection.createStatement(sql));
  }

  public Flux<Event> query(Connection connection, String tableName) {
    var sql = "SELECT %s FROM %s ORDER BY id".formatted(PAYLOAD_COL, tableName);
    var execute = connection.createStatement(sql).execute();
    return Flux.<Result>from(execute).flatMap(r -> r.map((row, rowMetadata) -> row)).map(this::rowToEvent);
  }

  public Event rowToEvent(Row row) {
    var eventString = row.get(PAYLOAD_COL, String.class);
    return this.stringTransformer.deserialize(eventString, Event.class).get();
  }
}
