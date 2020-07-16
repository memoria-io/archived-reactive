package io.memoria.jutils.core.messaging;

public record MessageFilter(String topic, int partition, long offset) {
  public MessageFilter(String topic) {
    this(topic, 0, 0);
  }
}
