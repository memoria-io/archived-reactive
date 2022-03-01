package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public record OMsg(long sKey, Id pKey, String value) implements Serializable {}
