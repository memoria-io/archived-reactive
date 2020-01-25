package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.repo;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.entity.MessageEntity;
import com.marmoush.jutils.general.domain.port.crud.EntityRepo;
import com.marmoush.jutils.general.domain.port.crud.EntityWriteRepo;

public interface MessageRepo extends EntityRepo<MessageEntity> {}
