package io.memoria.jutils.adapter.id;

import io.memoria.jutils.core.id.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

class IdGeneratorsTest {
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
}
