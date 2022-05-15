package io.memoria.reactive.core.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

class IdGeneratorTest {
  @Test
  void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get().value());
    Assertions.assertEquals("1", idGen.get().value());
    Assertions.assertEquals("2", idGen.get().value());
    idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get().value());
  }

  @Test
  void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.get().value().length());
    Assertions.assertEquals(5, idGen.get().value().split("-").length);
  }

  @Test
  void idTest() {
    UUID u = null;
    Assertions.assertThrows(NullPointerException.class, () -> Id.of(u));
    Assertions.assertThrows(IllegalArgumentException.class, () -> Id.of(""));
  }
}
