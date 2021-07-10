package io.memoria.jutils.jcore.stream;

import io.memoria.jutils.jcore.id.Id;

public record DefaultMsg(Id id, String body) implements Msg {}
