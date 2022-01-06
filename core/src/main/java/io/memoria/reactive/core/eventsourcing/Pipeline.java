package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.StreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.Function1;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class Pipeline {
  private final transient String topic;
  private final transient State initState;
  private final transient StateRepo stateRepo;
  private final transient StreamRepo eventStreamRepo;
  private final Decider decider;
  private final Evolver evolver;
  private final Saga saga;
  private final TextTransformer transformer;
  private final Function1<Msg, Mono<Integer>> publish;

  public Pipeline(String topic,
                  State initState,
                  StateRepo stateRepo,
                  StreamRepo eventStreamRepo,
                  Decider decider,
                  Evolver evolver,
                  Saga saga,
                  TextTransformer transformer) {
    this.topic = topic;
    this.initState = initState;
    this.stateRepo = stateRepo;
    this.eventStreamRepo = eventStreamRepo;
    this.publish = eventStreamRepo.publish(topic);
    this.decider = decider;
    this.evolver = evolver;
    this.saga = saga;
    this.transformer = transformer;
  }

  public Mono<Event> decide(Command cmd) {
    return getState(cmd.stateId().value()).flatMap(state -> decide(state, cmd));
  }

  public Flux<State> evolve(int skip) {
    return eventStreamRepo.subscribe(topic, skip).flatMap(this::toEvent).flatMap(this::evolve);
  }

  public Flux<Event> saga(int skip) {
    return eventStreamRepo.subscribe(topic, skip)
                          .flatMap(this::toEvent)
                          .flatMap(this::saga)
                          .filter(Option::isDefined)
                          .map(Option::get);
  }

  private Mono<Event> decide(State state, Command cmd) {
    var decision = decider.apply(state, cmd);
    var pub = eventStreamRepo.publish(topic);
    if (decision.isFailure()) {
      return Mono.error(decision.getCause());
    } else {
      var event = decision.get();
      return eventStreamRepo.size(topic).flatMap(idx -> toMsg(idx, event)).flatMap(pub).thenReturn(event);
    }
  }

  private Mono<State> evolve(Event event) {
    return getState(event.stateId().value()).flatMap(st -> evolveState(st, event));
  }

  private Mono<State> evolveState(State state, Event event) {
    var stateKey = event.stateId().value();
    var newState = evolver.apply(state, event);
    return stateRepo.put(stateKey, newState).thenReturn(newState);
  }

  private Mono<State> getState(String key) {
    return stateRepo.get(key).defaultIfEmpty(initState);
  }

  private Mono<Option<Event>> saga(Event event) {
    var sagaEventOpt = saga.apply(event);
    if (sagaEventOpt.isDefined()) {
      var sagaEvent = sagaEventOpt.get();
      return eventStreamRepo.size(topic)
                            .flatMap(idx -> toMsg(idx, sagaEvent))
                            .flatMap(publish)
                            .thenReturn(some(sagaEvent));
    } else {
      return Mono.just(none());
    }
  }

  private Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  private Mono<Msg> toMsg(int sKey, Event event) {
    return transformer.serialize(event).map(body -> new Msg(sKey, body));
  }
}
