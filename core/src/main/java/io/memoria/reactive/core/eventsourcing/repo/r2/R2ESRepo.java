package io.memoria.reactive.core.eventsourcing.repo.r2;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.repo.EventRepo;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.text.TextTransformer;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Result;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

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
  public Mono<List<Event>> find() {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      var sql = "SELECT %s FROM %s ORDER BY id".formatted(R2ESAdmin.PAYLOAD_COL, tableName);
      var execute = con.createStatement(sql).execute();
      return extractResult(execute);
    });
  }

  @Override
  public Mono<List<Event>> find(Id aggId) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      var sql = "SELECT %s FROM %s where %s=$1 ORDER BY id".formatted(R2ESAdmin.PAYLOAD_COL,
                                                                      tableName,
                                                                      R2ESAdmin.AGGREGATE_ID_COL);
      var execute = con.createStatement(sql).bind("$1", aggId.value()).execute();
      return extractResult(execute);
    });
  }

  private Mono<List<Event>> extractResult(Publisher<? extends Result> result) {
    return Flux.from(result)
               .flatMap(r -> r.map((row, rowMetadata) -> row))
               .map(row -> row.get(R2ESAdmin.PAYLOAD_COL, String.class))
               .map(row -> textTransformer.deserialize(row, Event.class))
               .map(Try::get)
               .collectList()
               .map(List::ofAll);
  }

  private Mono<Integer> insert(Connection connection, String tableName, List<Event> events) {
    var sql = "INSERT INTO %s (%s, %s, %s)".formatted(tableName,
                                                      R2ESAdmin.AGGREGATE_ID_COL,
                                                      R2ESAdmin.CREATED_AT_COL,
                                                      R2ESAdmin.PAYLOAD_COL) + " VALUES($1, $2, $3)";
    var st = connection.createStatement(sql);
    for (Event e : events) {
      var eventPayload = textTransformer.serialize(e).get();
      st.bind("$1", e.aggId().value()).bind("$2", Timestamp.valueOf(e.createdAt()).toString()).bind("$3", eventPayload);
      st.add();
    }
    return Mono.from(st.execute()).map(Result::getRowsUpdated).flatMap(Mono::from);
  }
}
