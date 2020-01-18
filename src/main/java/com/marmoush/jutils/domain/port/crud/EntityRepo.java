package com.marmoush.jutils.domain.port.crud;

import com.marmoush.jutils.domain.entity.Entity;

public interface EntityRepo<T extends Entity<?>> extends EntityWriteRepo<T>, EntityReadRepo<T> {}
