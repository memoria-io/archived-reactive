package io.memoria.jutils.core.adapter.generator.id;

import io.memoria.jutils.core.domain.port.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorsTest {
  @Test
  public void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get());
    Assertions.assertEquals("1", idGen.get());
    Assertions.assertEquals("2", idGen.get());
    idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get());
  }

  @Test
  public void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.get().length());
    Assertions.assertEquals(5, idGen.get().split("-").length);
  }
}
