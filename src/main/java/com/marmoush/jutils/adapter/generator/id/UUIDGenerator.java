package com.marmoush.jutils.adapter.generator.id;

import com.marmoush.jutils.domain.port.IdGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
