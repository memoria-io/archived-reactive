package com.marmoush.jutils.core.adapter.generator.id;

import com.marmoush.jutils.core.domain.port.IdGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
