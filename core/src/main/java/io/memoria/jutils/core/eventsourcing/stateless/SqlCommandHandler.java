package io.memoria.jutils.core.eventsourcing.stateless;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.Entity;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

/**
 * An SQL based commandHandler
 */
public final class SqlCommandHandler< S extends Entity<?>, C extends Command> implements CommandHandler<C> {
  private static final String ID_COL = "id";
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  private final PooledConnection pooledConnection;
  private final StringTransformer stringTransformer;
  private final S initialState;
  private final Evolver<S> evolver;
  private final Decider< S, C> decider;
  private final Scheduler scheduler;

  public SqlCommandHandler(PooledConnection pooledConnection,
                           StringTransformer stringTransformer,
                           S initialState,
                           Evolver< S> evolver,
                           Decider< S, C> decider,
                           Scheduler scheduler) {
    this.pooledConnection = pooledConnection;
    this.stringTransformer = stringTransformer;
    this.initialState = initialState;
    this.evolver = evolver;
    this.decider = decider;
    this.scheduler = scheduler;
  }

  @Override
  public Flux<Event> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var connection = this.pooledConnection.getConnection();
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(TRANSACTION_READ_COMMITTED);
      var tableName = toTableName(cmd.aggId().value());
      createTableIfNotExists(connection, tableName);
      var initialEvents = query(connection, tableName);
      var state = evolver.apply(initialState, initialEvents);
      var events = decider.apply(state, cmd).get();
      if (add(connection, tableName, events) == events.length()) {
        connection.commit();
        return events;
      } else {
        connection.rollback();
        throw new SQLException("Couldn't commit events, rolling back");
      }
    }).flatMapMany(Flux::fromIterable).subscribeOn(scheduler);
  }

  private int add(Connection connection, String tableName, List<Event> events) throws SQLException {
    var sql = "INSERT INTO %s (%s, %s, %s) ".formatted(tableName, ID_COL, CREATED_AT_COL, PAYLOAD_COL) +
              "VALUES(?, ?, ?)";
    var st = connection.prepareStatement(sql);
    for (Event e : events) {
      var eventPayload = this.stringTransformer.serialize(e).get();
      st.setString(1, e.eventId().value());
      st.setTimestamp(2, Timestamp.valueOf(e.createdAt()));
      st.setString(3, eventPayload);
      st.addBatch();
    }
    return st.executeBatch().length;
  }

  private List<Event> query(Connection connection, String tableName) throws SQLException {
    var sql = "Select %s from %s".formatted(PAYLOAD_COL, tableName);
    var resultSet = connection.prepareStatement(sql).executeQuery();
    var list = new ArrayList<Event>();
    while (resultSet.next()) {
      var eventString = resultSet.getString(PAYLOAD_COL);
      var event = this.stringTransformer.deserialize(eventString, Event.class).get();
      list.add(event);
    }
    return List.ofAll(list);
  }

  private static boolean createTableIfNotExists(Connection connection, String tableName) throws SQLException {
    var sql = """
              CREATE TABLE IF NOT EXISTS %s (
              id VARCHAR(36) NOT NULL,
              createdAt TIMESTAMP NOT NULL,
              payload TEXT NOT NULL,
              PRIMARY KEY (id)
            )
            """.formatted(tableName);
    return connection.prepareStatement(sql).execute();
  }

  // TODO tableName SQL Injection validation
  private static String toTableName(String value) {
    return value.replace(" ", "").replaceAll("[^A-Za-z0-9]", "");
  }
}
