package io.memoria.reactive.core.id;

record DefaultId(String value) implements Id {

  public DefaultId {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }
}
