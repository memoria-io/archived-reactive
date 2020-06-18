package io.memoria.jutils.messaging.domain.entity;

import io.memoria.jutils.core.domain.port.crud.Storable;

public record Msg(String id, String value) implements Storable<String> {}
