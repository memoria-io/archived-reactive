package io.memoria.jutils.messaging.domain;

import io.memoria.jutils.core.domain.port.crud.Storable;

public record Message(String id, String message) implements Storable<String> {}
