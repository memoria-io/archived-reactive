package io.memoria.reactive.core.stream;

import java.io.Serializable;

public record OMsg(int sKey, String value) implements Serializable {}
