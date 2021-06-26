package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Result;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

import static io.memoria.jutils.jcore.eventsourcing.repo.R2ESAdmin.AGGREGATE_ID_COL;
import static io.memoria.jutils.jcore.eventsourcing.repo.R2ESAdmin.CREATED_AT_COL;
import static io.memoria.jutils.jcore.eventsourcing.repo.R2ESAdmin.PAYLOAD_COL;

public record R2ESRepo(ConnectionFactory connectionFactory, String tableName, TextTransformer textTransformer)
        implements EventRepo {

  @Override
  public Mono<Integer> add(List<Event> events) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      // Configure Transaction
      con.setAutoCommit(false);
      con.beginTransaction();
      con.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED);
      // Insert events
      return insert(con, tableName, events).doOnSuccess(s -> con.commitTransaction())
                                           .doOnError(s -> con.rollbackTransaction());
    });
  }

  @Override
  public Mono<List<Event>> find(Id aggregate) {
    return Mono.from(connectionFactory.create()).flatMap(con -> query(con, aggregate));
  }

  private Mono<Integer> insert(Connection connection, String tableName, List<Event> events) {
    var sql = "INSERT INTO %s (%s, %s, %s)".formatted(tableName, AGGREGATE_ID_COL, CREATED_AT_COL, PAYLOAD_COL)
              + " VALUES($1, $2, $3)";
    var st = connection.createStatement(sql);
    for (Event e : events) {
      var eventPayload = textTransformer.serialize(e).get();
      st.bind("$1", e.aggId().value()).bind("$2", Timestamp.valueOf(e.createdAt()).toString()).bind("$3", eventPayload);
      st.add();
    }
    return Mono.from(st.execute()).map(Result::getRowsUpdated).flatMap(Mono::from);
  }

  private Mono<List<Event>> query(Connection connection, Id aggregate) {
    var sql = "SELECT %s FROM %s where %s=$1 ORDER BY id".formatted(PAYLOAD_COL, tableName, AGGREGATE_ID_COL);
    var execute = connection.createStatement(sql).bind("$1", aggregate.value()).execute();
    return Flux.<Result>from(execute)
               .flatMap(r -> r.map((row, rowMetadata) -> row))
               .map(row -> row.get(PAYLOAD_COL, String.class))
               .map(row -> textTransformer.deserialize(row, Event.class))
               .map(Try::get)
               .collectList()
               .map(List::ofAll);
  }
}
