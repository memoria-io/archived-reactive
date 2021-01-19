package io.memoria.jutils.eventsourcing.r2;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class R2Test {
  private final ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///testR2");
  private final String tableName = "test";

  @Test
  void connection() {
    var con = Mono.<Connection>from(connectionFactory.create());
    var sql = """
              CREATE TABLE IF NOT EXISTS %s (
              id INT GENERATED ALWAYS AS IDENTITY,
              payload TEXT NOT NULL,
              PRIMARY KEY (id)
            )
            """.formatted(tableName);
    var r1 = con.flatMap(c -> Mono.from(c.createStatement(sql).execute()))
                .map(Result::getRowsUpdated)
                .flatMap(Mono::from);
    StepVerifier.create(r1).expectNext(0).expectComplete().verify();

    // Insert
    var insertSql = "INSERT INTO %s (%s) ".formatted(tableName, "payload") + "VALUES($1)";
    var r2 = con.flatMap(c -> Mono.from(c.createStatement(insertSql).bind(0, "hello world").execute()))
                .map(Result::getRowsUpdated)
                .flatMap(Mono::from);
    StepVerifier.create(r2).expectNext(1).expectComplete().verify();

    // select
    var selectSql = "select * from %s".formatted(tableName);
    var r3 = con.flatMapMany(c -> Flux.from(c.createStatement(selectSql).execute()))
                .map(r -> r.map((row, rowMeta) -> row))
                .concatMap(Mono::from)
                .map(r -> r.get(0, Integer.class) + ":" + r.get(1, String.class))
                .doOnNext(System.out::println);
    StepVerifier.create(r3).expectNextCount(1).expectComplete().verify();
  }
}
