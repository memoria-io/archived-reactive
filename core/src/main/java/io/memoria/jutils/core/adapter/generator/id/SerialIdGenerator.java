package io.memoria.jutils.core.adapter.generator.id;

import io.memoria.jutils.core.domain.port.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class SerialIdGenerator implements IdGenerator<Long> {
  private final AtomicLong atomicLong;

  public SerialIdGenerator(AtomicLong atomicLong) {
    this.atomicLong = atomicLong;
  }

  @Override
  public Long generate() {
    return atomicLong.getAndIncrement();
  }
}
