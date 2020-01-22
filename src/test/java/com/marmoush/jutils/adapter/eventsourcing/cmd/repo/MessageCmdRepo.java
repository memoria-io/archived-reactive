package com.marmoush.jutils.adapter.eventsourcing.cmd.repo;

import com.marmoush.jutils.adapter.eventsourcing.cmd.entity.MessageEntity;
import com.marmoush.jutils.domain.port.crud.EntityWriteRepo;

public interface MessageCmdRepo extends EntityWriteRepo<MessageEntity> {}
