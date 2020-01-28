package com.marmoush.jutils.general.domain.port.crud;

import com.marmoush.jutils.general.domain.entity.Entity;

public interface EntityRepo<T extends Entity<?>> extends EntityWriteRepo<T>, EntityReadRepo<T> {}
