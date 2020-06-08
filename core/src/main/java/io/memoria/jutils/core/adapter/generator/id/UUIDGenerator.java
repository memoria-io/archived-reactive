package io.memoria.jutils.core.adapter.generator.id;

import io.memoria.jutils.core.domain.port.IdGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
