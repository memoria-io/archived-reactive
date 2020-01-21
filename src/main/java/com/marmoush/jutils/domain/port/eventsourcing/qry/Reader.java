package com.marmoush.jutils.domain.port.eventsourcing.qry;

import com.marmoush.jutils.domain.port.eventsourcing.DTO;
import reactor.core.publisher.Mono;

public interface Reader {
  Mono<DTO> read(Query query);
}
