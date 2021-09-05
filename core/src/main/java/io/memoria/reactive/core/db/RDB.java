package io.memoria.reactive.core.db;

public interface RDB<T> extends Pub<T>, Sub<T>, Write<T>, Read<T> {}
