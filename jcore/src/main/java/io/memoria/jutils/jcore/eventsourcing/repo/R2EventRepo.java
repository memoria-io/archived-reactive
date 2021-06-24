package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.sql.SqlUtils;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

import static io.memoria.jutils.jcore.sql.SqlUtils.safeTableName;

@SuppressWarnings("ClassCanBeRecord")
public class R2EventRepo implements EventRepo {
  private final ConnectionFactory connectionFactory;
  private final TextTransformer textTransformer;
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  public R2EventRepo(ConnectionFactory connectionFactory, TextTransformer textTransformer) {
    this.connectionFactory = connectionFactory;
    this.textTransformer = textTransformer;
  }

  @Override
  public Mono<Void> createTopic(String topic) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      // Configure Transaction
      con.setAutoCommit(false);
      con.beginTransaction();
      con.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED);
      var tableName = safeTableName(topic);
      var sql = """
                CREATE TABLE IF NOT EXISTS %s (
                id INT GENERATED ALWAYS AS IDENTITY,
                createdAt TIMESTAMP NOT NULL,
                payload TEXT NOT NULL,
                PRIMARY KEY (id)
              )
              """.formatted(tableName);
      return SqlUtils.exec(con.createStatement(sql)).last().doOnSuccess(i -> con.commitTransaction());
    }).then();
  }

  @Override
  public Mono<Integer> add(String topic, List<Event> events) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      // Configure Transaction
      con.setAutoCommit(false);
      con.beginTransaction();
      con.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED);
      // Append events
      var st = Mono.from(insert(con, topic, events).execute()).map(Result::getRowsUpdated).flatMap(Mono::from);
      return st.doOnSuccess(s -> con.commitTransaction()).doOnError(s -> con.rollbackTransaction());
    });
  }

  @Override
  public Mono<List<Event>> find(String topic) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      var tableName = safeTableName(topic);
      return query(con, topic).collectList();
    }).map(List::ofAll);
  }

  private Flux<Event> query(Connection connection, String tableName) {
    var sql = "SELECT %s FROM %s ORDER BY id".formatted(PAYLOAD_COL, tableName);
    var execute = connection.createStatement(sql).execute();
    return Flux.<Result>from(execute).flatMap(r -> r.map((row, rowMetadata) -> row)).map(this::rowToEvent);
  }

  private Event rowToEvent(Row row) {
    var eventString = row.get(PAYLOAD_COL, String.class);
    return this.textTransformer.deserialize(eventString, Event.class).get();
  }

  private Statement insert(Connection connection, String tableName, List<Event> events) {
    var sql = "INSERT INTO %s (%s, %s) ".formatted(tableName, CREATED_AT_COL, PAYLOAD_COL) + "VALUES($1, $2)";
    var st = connection.createStatement(sql);
    for (Event e : events) {
      var eventPayload = this.textTransformer.serialize(e).get();
      st.bind("$1", Timestamp.valueOf(e.createdAt()).toString()).bind("$2", eventPayload);
      st.add();
    }
    return st;
  }
}
