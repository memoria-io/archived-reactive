package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.nats.client.Connection;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class NatsConnection {
  public static final String CHANNEL_SEPARATOR = ".";
  private static final Logger log = LoggerFactory.getLogger(NatsConnection.class.getName());

  private NatsConnection() {}

  public static Connection create(YamlConfigMap c) throws IOException, InterruptedException {
    var nats = c.asYamlConfigMap("nats");
    var server = nats.asString("server");
    var conTimeout = Duration.ofMillis(nats.asLong("connectionTimeout"));
    var reconTimeout = Duration.ofMillis(nats.asLong("reconnectionTimeout"));
    var pingInterval = Duration.ofMillis(nats.asLong("pingInterval"));
    var bufferSize = nats.asInteger("bufferSize");

    var config = new Options.Builder().server(server)
                                      .connectionTimeout(conTimeout)
                                      .reconnectWait(reconTimeout)
                                      .bufferSize(bufferSize)
                                      .pingInterval(pingInterval)
                                      .connectionListener((conn, type) -> log.info(String.format("Status change %s ",
                                                                                                 type)))
                                      .errorListener(err)
                                      .build();
    return Nats.connect(config);
  }

  private static ErrorListener err = new ErrorListener() {
    public void exceptionOccurred(Connection conn, Exception exp) {
      log.error("Exception", exp);
    }

    public void errorOccurred(Connection conn, String type) {
      log.error("Error {}", type);
    }

    public void slowConsumerDetected(Connection conn, Consumer consumer) {
      var url = Option.of(conn.getConnectedUrl()).getOrElse("");
      log.error("Slow consumer on connection {}", url);
    }
  };
}
