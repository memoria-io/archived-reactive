package io.memoria.reactive.core.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

class IdGeneratorTest {
  @Test
  void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get().id());
    Assertions.assertEquals("1", idGen.get().id());
    Assertions.assertEquals("2", idGen.get().id());
    idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get().id());
  }

  @Test
  void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.get().id().length());
    Assertions.assertEquals(5, idGen.get().id().split("-").length);
  }

  @Test
  void idTest() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Id(null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Id(""));
  }
}
