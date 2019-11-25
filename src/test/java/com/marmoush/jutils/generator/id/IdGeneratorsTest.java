package com.marmoush.jutils.generator.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGeneratorsTest {
  @Test
  public void SerialIdTest() {
    IdGenerator idGen = new SerialIdGenerator(new AtomicInteger());
    Assertions.assertEquals(idGen.generate(), "0");
    Assertions.assertEquals(idGen.generate(), "1");
    Assertions.assertEquals(idGen.generate(), "2");
    idGen = new SerialIdGenerator(new AtomicInteger());
    Assertions.assertEquals(idGen.generate(), "0");
  }

  @Test
  public void UUIDTest() {
    IdGenerator idGen = new UUIDGenerator();
    Assertions.assertEquals(36, idGen.generate().length());
    Assertions.assertEquals(5, idGen.generate().split("-").length);
  }
}
