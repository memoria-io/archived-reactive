package io.memoria.reactive.core.db;

public interface RDB<T extends Msg> extends Pub<T>, Sub<T>, Write<T>, Read<T> {}
