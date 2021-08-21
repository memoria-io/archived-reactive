package io.memoria.reactive.core.stream;

public interface StreamDB<T> extends Pub<T>, Sub<T>, Write<T>, Read<T> {}
