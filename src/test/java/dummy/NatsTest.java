package dummy;

import io.nats.client.*;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static io.nats.client.Options.DEFAULT_URL;
import static java.lang.System.out;

public class NatsTest {

  public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
    String server = DEFAULT_URL;
    String subject = "my_subject";
    String message = "Hello world";
    int elements = 3;
    Connection nc = Nats.connect(createExampleOptions(server));
    Flux<Integer> pub = Flux.range(0, elements)
                            .doOnNext(i -> nc.publish(subject, message.getBytes(StandardCharsets.UTF_8)));

    Subscription sub = nc.subscribe(subject);
    Flux<Try<String>> consumer = Flux.interval(Duration.ofSeconds(1))
                                     .take(elements)
                                     .map(i -> Try.of(() -> sub.nextMessage(Duration.ofMillis(500))))
                                     .map(t -> t.map(msg -> new String(msg.getData())))
                                     .doOnNext(t -> out.println(t.get()));

    StepVerifier.create(pub).expectNextCount(elements).expectComplete().verify();
    StepVerifier.create(consumer).expectNextCount(elements).expectComplete().verify();
    nc.flush(Duration.ofSeconds(1));
    nc.close();
  }

  private static Options createExampleOptions(String server) {
    return new Options.Builder().server(server)
                                .connectionTimeout(Duration.ofSeconds(5))
                                .pingInterval(Duration.ofSeconds(10))
                                .reconnectWait(Duration.ofSeconds(1))
                                .errorListener(err)
                                .connectionListener((conn, type) -> out.println("Status change " + type))
                                .build();
  }

  private static ErrorListener err = new ErrorListener() {
    public void exceptionOccurred(Connection conn, Exception exp) {
      out.println("Exception " + exp.getMessage());
    }

    public void errorOccurred(Connection conn, String type) {
      out.println("Error " + type);
    }

    public void slowConsumerDetected(Connection conn, Consumer consumer) {
      out.println("Slow consumer");
    }
  };
}
