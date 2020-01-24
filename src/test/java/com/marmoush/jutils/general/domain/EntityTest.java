package com.marmoush.jutils.general.domain;

import com.marmoush.jutils.general.domain.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityTest {
  @Test
  @DisplayName("Entities with same value should be equal")
  public void equalEntities() {
    Assertions.assertEquals(new Entity<>("hello", 1), new Entity<>("hello", 1));
    Assertions.assertNotEquals(new Entity<>("hello", 1), new Entity<>("hello", 2));
    Assertions.assertNotEquals(new Entity<>("hello", 1), new Entity<>("hi", 1));
    Assertions.assertNotEquals(new Entity<>("hello", 1), new Entity<>("hi", 2));
    Assertions.assertThrows(NullPointerException.class,
                            () -> new Entity<>("hello", null).equals(new Entity<>("hello", 1)));
  }
}
