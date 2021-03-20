package io.memoria.jutils.jes.r2;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

public final class R2Connection {
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  private final Connection connection;
  private final String tableName;
  private final TextTransformer textTransformer;

  public R2Connection(Connection connection, String tableName, TextTransformer textTransformer) {
    this.connection = connection;
    this.tableName = tableName;
    this.textTransformer = textTransformer;
  }

  public Mono<Integer> appendEvents(List<Event> events) {
    return Mono.fromCallable(() -> insert(events))
               .map(Statement::execute)
               .flatMap(Mono::from)
               .map(Result::getRowsUpdated)
               .flatMap(Mono::from);
  }

  public Mono<Integer> createTableIfNotExists() {
    var sql = """
              CREATE TABLE IF NOT EXISTS %s (
              id INT GENERATED ALWAYS AS IDENTITY,
              createdAt TIMESTAMP NOT NULL,
              payload TEXT NOT NULL,
              PRIMARY KEY (id)
            )
            """.formatted(tableName);
    return SqlUtils.exec(connection.createStatement(sql)).last();
  }

  public Flux<Event> query() {
    var sql = "SELECT %s FROM %s ORDER BY id".formatted(PAYLOAD_COL, tableName);
    var execute = connection.createStatement(sql).execute();
    return Flux.<Result>from(execute).flatMap(r -> r.map((row, rowMetadata) -> row)).map(this::rowToEvent);
  }

  public Event rowToEvent(Row row) {
    var eventString = row.get(PAYLOAD_COL, String.class);
    return this.textTransformer.deserialize(eventString, Event.class).get();
  }

  private Statement insert(List<Event> events) {
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
