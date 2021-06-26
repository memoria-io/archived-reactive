package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.sql.SqlUtils;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

public class R2ESAdmin {
  public static final String AGGREGATE_ID_COL = "aggregate_id";
  public static final String CREATED_AT_COL = "created_at";
  public static final String PAYLOAD_COL = "payload";

  public static Mono<Void> createTableIfNotExists(ConnectionFactory connectionFactory, String tableName) {
    return Mono.from(connectionFactory.create()).flatMap(con -> {
      con.setAutoCommit(true);
      var sql = """
                CREATE TABLE IF NOT EXISTS %s (
                id INT GENERATED ALWAYS AS IDENTITY,
                %s varchar(36) NOT NULL,
                %s TIMESTAMP NOT NULL,
                %s TEXT NOT NULL,
                PRIMARY KEY (id)
              )
              """.formatted(tableName, AGGREGATE_ID_COL, CREATED_AT_COL, PAYLOAD_COL);
      return SqlUtils.exec(con.createStatement(sql)).last();
    }).then();
  }

  private R2ESAdmin() {}
}
