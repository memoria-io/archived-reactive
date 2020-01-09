package dummy;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class DummyTest {
  public static void main(String[] args) {

  }

  private void fluxInterval() {
    Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                              .take(10)
                              .concatWith(Flux.just(4l, 5l, 6l))
                              .doOnNext(System.out::println);
    StepVerifier.create(longFlux).expectNextCount(13).expectComplete().verify();
  }

  private void fluxGen2() {
    Flux.generate(sink -> sink.next(1)).subscribe(System.out::println);
  }

  private void fluxGen() {
    Flux.<String, Integer>generate(() -> 0, (i, sink) -> {
      sink.next(i + "$");
      if (i == 10) {
        sink.complete();
      }
      return ++i;
    }).subscribe(System.out::println);
  }

  private void flatMapGen() {
    var atom = new AtomicInteger();
    var f = Flux.<List<Integer>, List<Integer>>generate(() -> List.empty(), (i, sink) -> {
      sink.next(i);
      if (atom.getAndIncrement() >= 3)
        sink.complete();
      return poll();
    });
    f.subscribe(s -> {
      System.out.println("--------");
      s.forEach(System.out::println);
    });
    // f.reduce(List.of(0), (l1, l2) -> l1.appendAll(l2)).block().forEach(System.out::println);
    //    f.reduce(Flux.just(Try.success(0)), (f1, f2) -> f1.thenMany(f2)).block().subscribe(t -> System.out.println(t.get()));
  }

  private List<Integer> poll() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return List.of(1, 2, 3);
  }
}
