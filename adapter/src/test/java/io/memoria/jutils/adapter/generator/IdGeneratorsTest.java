package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

class IdGeneratorsTest {
  @Test
  void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get());
    Assertions.assertEquals("1", idGen.get());
    Assertions.assertEquals("2", idGen.get());
    idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get());
  }

  @Test
  void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.get().length());
    Assertions.assertEquals(5, idGen.get().split("-").length);
  }
}
