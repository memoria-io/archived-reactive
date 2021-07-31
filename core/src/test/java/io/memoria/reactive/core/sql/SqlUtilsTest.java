package io.memoria.reactive.core.sql;

import io.r2dbc.spi.ConnectionFactories;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class SqlUtilsTest {

  @Test
  void drop() {
    // Given
    var con = ConnectionFactories.get("r2dbc:h2:mem:///testR2");
    var create = """
              CREATE TABLE IF NOT EXISTS %s (
              id INT GENERATED ALWAYS AS IDENTITY,
              payload TEXT NOT NULL,
              PRIMARY KEY (id)
            )
            """.formatted("sometable");
    var insert = "insert into sometable (payload) values('hello world')";
    SqlUtils.exec(con, create).subscribe();
    SqlUtils.exec(con, insert).repeat(2).subscribe();

    // When
    var select = SqlUtils.query(con, "Select * from sometable");

    // Then
    StepVerifier.create(select).expectNextCount(3).expectComplete().verify();
    StepVerifier.create(SqlUtils.dropObjects(con)).expectComplete().verify();
    StepVerifier.create(select).expectError().verify();
  }
}