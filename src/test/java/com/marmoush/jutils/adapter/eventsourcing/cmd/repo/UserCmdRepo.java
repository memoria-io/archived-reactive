package com.marmoush.jutils.adapter.eventsourcing.cmd.repo;

import com.marmoush.jutils.adapter.eventsourcing.cmd.entity.UserEntity;
import com.marmoush.jutils.domain.port.crud.EntityWriteRepo;

public interface UserCmdRepo extends EntityWriteRepo<UserEntity> {}
