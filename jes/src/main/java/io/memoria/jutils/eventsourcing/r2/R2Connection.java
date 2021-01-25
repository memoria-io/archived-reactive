package io.memoria.jutils.eventsourcing.r2;

import io.memoria.jutils.eventsourcing.Event;
import io.memoria.jutils.jtext.transformer.StringTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

public final class R2Connection {
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  private final Connection connection;
  private final String tableName;
  private final StringTransformer stringTransformer;

  public R2Connection(Connection connection, String tableName, StringTransformer stringTransformer) {
    this.connection = connection;
    this.tableName = tableName;
    this.stringTransformer = stringTransformer;
  }

  public Mono<Integer> appendEvents(List<Event> events) {
    var sql = "INSERT INTO %s (%s, %s) ".formatted(tableName, CREATED_AT_COL, PAYLOAD_COL) + "VALUES($1, $2)";
    var st = connection.createStatement(sql);
    for (Event e : events) {
      var eventPayload = this.stringTransformer.serialize(e).get();
      st.bind("$1", Timestamp.valueOf(e.createdAt()).toString()).bind("$2", eventPayload);
      st.add();
    }
    return Mono.<Result>from(st.execute()).flatMap(r -> Mono.from(r.getRowsUpdated()));
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
    return this.stringTransformer.deserialize(eventString, Event.class).get();
  }
}
