package com.marmoush.jutils.core.domain.port.crud;

import com.marmoush.jutils.core.domain.entity.Entity;

public interface EntityRepo<T extends Entity<?>> extends EntityWriteRepo<T>, EntityReadRepo<T> {}
