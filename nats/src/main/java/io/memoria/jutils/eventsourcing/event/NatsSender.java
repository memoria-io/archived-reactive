//package io.memoria.jutils.eventsourcing.event;
//
//import io.nats.client.Connection;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Scheduler;
//
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//
//public record NatsSender(Connection nc, MessageLocation mf, Scheduler scheduler, Duration timeout) implements MsgSender {
//
//  @Override
//  public Mono<Response> apply(Message message) {
//    return Mono.fromCallable(() -> publish(message)).subscribeOn(scheduler).timeout(timeout);
//  }
//
//  private Response publish(Message msg) {
//    var subj = NatsUtils.toSubject(mf.topic(), mf.partition());
//    var msgStr = msg.value().getBytes(StandardCharsets.UTF_8);
//    nc.publish(subj, msgStr);
//    return Response.empty();
//  }
//}
