package io.memoria.jutils.messaging.domain;

import io.vavr.control.Option;

public record Message(Option<String>id, String message) {}
