package com.marmoush.jutils.adapter.msgbus.nats;

import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.nats.client.*;
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
    var nats = c.asMap("nats");
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
      log.error(String.format("Error %s ", type));
    }

    public void slowConsumerDetected(Connection conn, Consumer consumer) {
      var url = Option.of(conn.getConnectedUrl()).getOrElse("");
      log.error(String.format("Slow consumer on connection %s", url));
    }
  };
}
