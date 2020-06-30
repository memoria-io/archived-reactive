package io.memoria.jutils.core.adapter.generator.id;

import io.memoria.jutils.core.domain.port.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public record SerialIdGenerator(AtomicLong atomicLong) implements IdGenerator {

  @Override
  public String get() {
    return atomicLong.getAndIncrement() + "";
  }
}
