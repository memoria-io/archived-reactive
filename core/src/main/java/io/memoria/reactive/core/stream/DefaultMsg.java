package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;

public record DefaultMsg(Id id, String body) implements Msg {}
