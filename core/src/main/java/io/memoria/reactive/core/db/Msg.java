package io.memoria.reactive.core.db;

public record Msg<T>(long id, T body) {}
