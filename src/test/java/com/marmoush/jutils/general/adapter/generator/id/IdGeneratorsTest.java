package com.marmoush.jutils.general.adapter.generator.id;

import com.marmoush.jutils.general.domain.port.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGeneratorsTest {
  @Test
  public void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicInteger());
    Assertions.assertEquals("0", idGen.generate());
    Assertions.assertEquals("1", idGen.generate());
    Assertions.assertEquals("2", idGen.generate());
    idGen = new SerialIdGenerator(new AtomicInteger());
    Assertions.assertEquals("0", idGen.generate());
  }

  @Test
  public void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.generate().length());
    Assertions.assertEquals(5, idGen.generate().split("-").length);
  }
}
