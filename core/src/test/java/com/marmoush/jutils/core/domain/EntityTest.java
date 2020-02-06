package com.marmoush.jutils.core.domain;

import com.marmoush.jutils.core.domain.entity.Entity;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static io.vavr.control.Option.none;

public class EntityTest {
  @Test
  @DisplayName("Entities with same value should be equal")
  public void equalEntities() {
    var id1 = "1";
    var id2 = "2";
    var hello = "hello";
    var hi = "hi";
    var time = LocalDateTime.now();
    Assertions.assertEquals(new Entity<>(id1, hello, time, none()), new Entity<>(id1, hello, time, none()));
    Assertions.assertNotEquals(new Entity<>(id1, hello, time, none()), new Entity<>(id1, hi));
    Assertions.assertNotEquals(new Entity<>(id1, hello, time, none()), new Entity<>(id2, hello, time, none()));
    Assertions.assertNotEquals(new Entity<>(id1, hello, time, none()), new Entity<>(id2, hi, time, none()));
    Assertions.assertThrows(NullPointerException.class,
                            () -> new Entity<>(id1, null, time, none()).equals(new Entity<>(id1, hello, time, none())));
  }
}
