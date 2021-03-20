package io.memoria.jutils.jpostgres.r2;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.EventSubscriber;
import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class R2EventSubscriber implements EventSubscriber {
  private final PostgresqlConnection connectionFactory;
  private final TextTransformer textTransformer;

  public R2EventSubscriber(PostgresqlConnection connectionFactory, TextTransformer textTransformer) {
    this.connectionFactory = connectionFactory;
    this.textTransformer = textTransformer;
  }

  @Override
  public Mono<Boolean> exists(Id aggId) {
    return null;
  }

  @Override
  public <E extends Event> Flux<E> subscribe(Id aggId, long offset, Class<E> as) {
    return null;
  }
}
