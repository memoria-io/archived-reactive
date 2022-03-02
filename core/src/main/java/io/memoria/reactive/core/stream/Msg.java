package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public record Msg(String topic, int partition, Id id, String value) implements Serializable {}
