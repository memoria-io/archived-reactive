package com.marmoush.jutils.general.adapter.generator.id;

import com.marmoush.jutils.general.domain.port.IdGenerator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
