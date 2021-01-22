package io.memoria.jutils.eventsourcing.r2;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SqlUtils {
  public static final String DROP_OBJECTS = "DROP ALL OBJECTS";

  public static Mono<Void> dropObjects(ConnectionFactory con) {
    return exec(con, DROP_OBJECTS).then();
  }

  public static Flux<Integer> exec(Statement st) {
    return Flux.from(st.execute()).flatMap(s -> Flux.from(s.getRowsUpdated()));
  }

  public static Mono<Integer> exec(ConnectionFactory con, String sql) {
    return Mono.from(con.create())
               .flatMap(c -> Mono.<Result>from(c.createStatement(sql).execute()))
               .map(Result::getRowsUpdated)
               .flatMap(Mono::from);
  }

  public static Flux<Row> query(ConnectionFactory con, String sql) {
    return Mono.from(con.create())
               .flatMapMany(c -> Flux.<Result>from(c.createStatement(sql).execute()))
               .flatMap(r -> Flux.from(r.map((row, rowMetadata) -> row)));
  }

  public static String safeTableName(String value) {
    return value.replace(" ", "").replaceAll("[^A-Za-z0-9]", "");
  }
}
