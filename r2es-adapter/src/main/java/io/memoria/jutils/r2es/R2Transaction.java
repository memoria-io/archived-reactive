package io.memoria.jutils.r2es;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

import static io.memoria.jutils.r2es.R2Utils.executeUpdate;

public class R2Transaction {
  private static final Logger log = LoggerFactory.getLogger(R2Transaction.class.getName());
  private static final String CREATED_AT_COL = "createdAt";
  private static final String PAYLOAD_COL = "payload";

  private final Connection connection;
  private final String tableName;
  private final StringTransformer stringTransformer;

  public R2Transaction(Connection connection, String tableName, StringTransformer stringTransformer) {
    this.connection = connection;
    R2Utils.setTransactionConfigs(connection);
    this.tableName = tableName;
    this.stringTransformer = stringTransformer;
  }

  public Mono<Integer> appendEvents(List<Event> events) {
    var sql = "INSERT INTO %s (%s, %s) ".formatted(this.tableName, CREATED_AT_COL, PAYLOAD_COL) + "VALUES($1, $2)";
    var st = this.connection.createStatement(sql);
    for (Event e : events) {
      var eventPayload = this.stringTransformer.serialize(e).get();
      st.bind("$1", Timestamp.valueOf(e.createdAt()).toString()).bind("$2", eventPayload);
      st.add();
    }
    return executeUpdate(st);
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
    return executeUpdate(this.connection.createStatement(sql));
  }

  public Flux<Event> query() {
    var sql = "SELECT %s FROM %s ORDER BY id".formatted(PAYLOAD_COL, tableName);
    var execute = connection.createStatement(sql).execute();
    return Flux.<Result>from(execute).flatMap(r -> r.map((row, rowMetadata) -> row)).map(this::rowToEvent);
  }

  public Mono<Void> rollback() {
    return Mono.from(this.connection.rollbackTransaction()).doOnSuccess(v -> log.info("Rolling back updates"));
  }

  private Event rowToEvent(Row row) {
    var eventString = row.get(PAYLOAD_COL, String.class);
    return this.stringTransformer.deserialize(eventString, Event.class).get();
  }
}

  

  
