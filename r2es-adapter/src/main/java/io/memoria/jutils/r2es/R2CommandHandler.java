package io.memoria.jutils.r2es;

import io.memoria.jutils.core.JutilsException;
import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Decider;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.eventsourcing.Evolver;
import io.memoria.jutils.core.transformer.StringTransformer;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;

/**
 * An SQL based commandHandler
 */
public final class R2CommandHandler<S, C extends Command> implements CommandHandler<C> {

  private final ConnectionFactory connectionFactory;
  private final StringTransformer stringTransformer;
  private final S initialState;
  private final Evolver<S> evolver;
  private final Decider<S, C> decider;

  public R2CommandHandler(ConnectionFactory connectionFactory,
                          StringTransformer stringTransformer,
                          S initialState,
                          Evolver<S> evolver,
                          Decider<S, C> decider) {
    this.connectionFactory = connectionFactory;
    this.stringTransformer = stringTransformer;
    this.initialState = initialState;
    this.evolver = evolver;
    this.decider = decider;
  }

  @Override
  public Flux<Event> apply(C cmd) {
    return Mono.from(connectionFactory.create()).flatMapMany(con -> {
      var transaction = new R2Transaction(con, toTableName(cmd.aggId().value()), stringTransformer);
      var initialEventsFlux = transaction.createTableIfNotExists().thenMany(transaction.query());
      var stateMono = evolver.apply(initialState, initialEventsFlux);
      var eventsMono = stateMono.flatMap(s -> toMono(decider.apply(s, cmd)));
      return eventsMono.flatMapMany(events -> transaction.appendEvents(events)
                                                         .flatMap(validate(i -> i.equals(events.size())))
                                                         .flatMapMany(v -> Flux.fromIterable(events)))
                       .doOnError(t -> transaction.rollback());
    });
  }

  private Function<Integer, Mono<Void>> validate(Predicate<Integer> predicate) {
    return i -> (predicate.test(i)) ? Mono.empty()
                                    : Mono.error(JutilsException.validationError("Statement Execution failed"));
  }

  // TODO tableName SQL Injection validation
  private static String toTableName(String value) {
    return value.replace(" ", "").replaceAll("[^A-Za-z0-9]", "");
  }
}
