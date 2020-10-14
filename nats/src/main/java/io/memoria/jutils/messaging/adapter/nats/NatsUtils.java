package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.transformer.Properties;
import io.nats.client.Connection;
import io.nats.client.ConnectionListener.Events;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class NatsUtils {
  public static final String CHANNEL_SEPARATOR = ".";
  private static final Logger log = LoggerFactory.getLogger(NatsUtils.class.getName());
  private static final ErrorListener err = new ErrorListener() {
    public void errorOccurred(Connection conn, String type) {
      log.error("Error {}", type);
    }

    public void exceptionOccurred(Connection conn, Exception exp) {
      log.error("Exception", exp);
    }

    public void slowConsumerDetected(Connection conn, Consumer consumer) {
      var url = Option.of(conn.getConnectedUrl()).getOrElse("");
      log.error("Slow consumer on connection {}", url);
    }
  };

  public static Connection createConnection(Properties c) throws IOException, InterruptedException {
    var nats = c.sub("nats").get();
    var server = nats.asString("server").get();
    var conTimeout = Duration.ofMillis(nats.asLong("connectionTimeout").get());
    var reconTimeout = Duration.ofMillis(nats.asLong("reconnectionTimeout").get());
    var pingInterval = Duration.ofMillis(nats.asLong("pingInterval").get());
    var bufferSize = nats.asInteger("bufferSize").get();

    var config = new Options.Builder().server(server)
                                      .connectionTimeout(conTimeout)
                                      .reconnectWait(reconTimeout)
                                      .bufferSize(bufferSize)
                                      .pingInterval(pingInterval)
                                      .connectionListener(NatsUtils::onConnectionEvent)
                                      .errorListener(err)
                                      .build();
    return Nats.connect(config);
  }

  public static Message toMessage(io.nats.client.Message message) {
    return new Message(new String(message.getData()));
  }

  public static String toSubject(String topic, int partition) {
    return topic + CHANNEL_SEPARATOR + partition;
  }

  private NatsUtils() {}

  private static void onConnectionEvent(Connection conn, Events type) {
    log.info("Status change {}", type);
  }
}
