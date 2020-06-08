package io.memoria.jutils.core.domain.port.crud;

import io.memoria.jutils.core.domain.entity.Entity;

public interface EntityRepo<T extends Entity<?>> extends EntityWriteRepo<T>, EntityReadRepo<T> {}
