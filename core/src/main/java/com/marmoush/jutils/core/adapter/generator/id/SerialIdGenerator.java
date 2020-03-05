package com.marmoush.jutils.core.adapter.generator.id;

import com.marmoush.jutils.core.domain.port.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class SerialIdGenerator implements IdGenerator {
  private final AtomicLong atomicLong;

  public SerialIdGenerator(AtomicLong atomicLong) {
    this.atomicLong = atomicLong;
  }

  @Override
  public String generate() {
    return atomicLong.getAndIncrement() + "";
  }
}
