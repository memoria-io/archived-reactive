package com.marmoush.jutils.core.adapter.generator.id;

import com.marmoush.jutils.core.domain.port.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorsTest {
  @Test
  public void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.generate());
    Assertions.assertEquals("1", idGen.generate());
    Assertions.assertEquals("2", idGen.generate());
    idGen = new SerialIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.generate());
  }

  @Test
  public void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.generate().length());
    Assertions.assertEquals(5, idGen.generate().split("-").length);
  }
}
