package io.memoria.reactive.core.stream;

import java.io.Serializable;

public record OMsg(long sKey, String value) implements Serializable {}
