package com.marmoush.jutils.adapter.generator.id;

import com.marmoush.jutils.domain.port.IdGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class SerialIdGenerator implements IdGenerator {
  private final AtomicInteger atomicInteger;

  public SerialIdGenerator(AtomicInteger atomicInteger) {
    this.atomicInteger = atomicInteger;
  }

  @Override
  public String generate() {
    return atomicInteger.getAndIncrement() + "";
  }
}
