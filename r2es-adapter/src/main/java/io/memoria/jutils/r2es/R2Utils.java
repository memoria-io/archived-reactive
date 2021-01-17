package io.memoria.jutils.r2es;

import io.memoria.jutils.core.eventsourcing.ESException;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

public class R2Utils {
  public static ConnectionFactory createConnection(String driver,
                                                   String host,
                                                   int port,
                                                   String username,
                                                   String password,
                                                   String db) {
    return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                                                           .option(DRIVER, driver)
                                                           .option(HOST, host)
                                                           .option(PORT, port)
                                                           .option(USER, username)
                                                           .option(PASSWORD, password)
                                                           .option(DATABASE, db)
                                                           .build());
  }

  public static Mono<Integer> executeUpdate(Statement st) {
    return Mono.from(st.execute()).flatMap(s -> Mono.from(s.getRowsUpdated()));
  }


  private R2Utils() {}

  // TODO tableName SQL Injection validation
  static String toTableName(String value) {
    return value.replace(" ", "").replaceAll("[^A-Za-z0-9]", "");
  }
}
