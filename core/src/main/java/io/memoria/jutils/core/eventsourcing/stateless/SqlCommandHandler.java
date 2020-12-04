package io.memoria.jutils.core.eventsourcing.stateless;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.State;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.value.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class SqlCommandHandler<S extends State, C extends Command> implements CommandHandler<S, C> {
  private static final Logger log = LoggerFactory.getLogger(SqlCommandHandler.class.getName());
  private static final String ID_COL = "id";
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  private final PooledConnection pooledConnection;
  private final StringTransformer stringTransformer;
  private final Scheduler scheduler;

  public SqlCommandHandler(PooledConnection pooledConnection,
                           StringTransformer stringTransformer,
                           Scheduler scheduler) {
    this.pooledConnection = pooledConnection;
    this.stringTransformer = stringTransformer;
    this.scheduler = scheduler;
  }

  @Override
  public Flux<Event> apply(Id id, C c) {
    Mono.fromCallable(() -> {
      // Connection
      var connection = this.pooledConnection.getConnection();
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(TRANSACTION_READ_COMMITTED);
      // table
      var tableName = toTableName(id.value());

      var events = query(connection, tableName, id);

      var stateMono = eventStore.get(id).map(events -> evolver.apply(initialState, events));
      var eventsFlux = stateMono.flatMapMany(state -> toFlux(decider.apply(state, cmd)));
      var result = eventsFlux.concatMap(e -> eventStore.add(id, e)).collectList();
    });
    return null;
  }

  private int add(Connection connection, String tableName, List<Event> events) throws SQLException {
    var sql = "INSERT INTO %s (%s, %s, %s) ".formatted(tableName, ID_COL, CREATED_AT_COL, PAYLOAD_COL) +
              "VALUES(?, ?, ?)";
    var st = connection.prepareStatement(sql);
    for (Event e : events) {
      var eventPayload = this.stringTransformer.serialize(e).get();
      st.setString(1, tableName);
      st.setTimestamp(2, Timestamp.valueOf(e.createdAt()));
      st.setString(3, eventPayload);
      st.addBatch();
    }
    return st.executeBatch().length;
  }

  private List<Event> query(Connection connection, String tableName, Id aggId) throws SQLException {
    var sql = "Select %s from %s where aggId = ?".formatted(PAYLOAD_COL, tableName);
    var st = connection.prepareStatement(sql);
    st.setString(1, aggId.value());
    var resultSet = st.executeQuery();
    List<Event> list = new ArrayList<>();
    while (resultSet.next()) {
      var eventString = resultSet.getString(PAYLOAD_COL);
      var event = this.stringTransformer.deserialize(eventString, Event.class).get();
      list.add(event);
    }
    return list;
  }

  Mono<Boolean> tableExists(Connection connection, String tableName) {
    var sql = """
              CREATE TABLE IF NOT EXISTS %s (
              id VARCHAR(36) NOT NULL,
              createdAt TIMESTAMP NOT NULL,
              payload TEXT NOT NULL,
              PRIMARY KEY (id)
            )
            """.formatted(tableName);
    return Mono.fromCallable(() -> connection.prepareStatement(sql).execute()).subscribeOn(scheduler);
  }

  // TODO tableName SQL Injection validation
  private static String toTableName(String value) {
    return value.replace(" ", "").replaceAll("[^A-Za-z0-9]", "");
  }
}
