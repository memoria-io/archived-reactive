package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

class IdGeneratorsTest {
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
}
