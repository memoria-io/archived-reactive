package com.marmoush.jutils.core.domain;

import com.marmoush.jutils.core.domain.entity.Entity;
import org.junit.jupiter.api.*;

public class EntityTest {
  @Test
  @DisplayName("Entities with same value should be equal")
  public void equalEntities() {
    var id1 = "1";
    var id2 = "2";
    var hello = "hello";
    var hi = "hi";
    Assertions.assertEquals(new Entity<>(id1, hello), new Entity<>(id1, hello));
    Assertions.assertNotEquals(new Entity<>(id1, hello), new Entity<>(id1, hi));
    Assertions.assertNotEquals(new Entity<>(id1, hello), new Entity<>(id2, hello));
    Assertions.assertNotEquals(new Entity<>(id1, hello), new Entity<>(id2, hi));
    Assertions.assertThrows(NullPointerException.class, () -> new Entity<>(id1, null).equals(new Entity<>(id1, hello)));
  }
}
