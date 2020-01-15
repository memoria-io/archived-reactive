package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.nats.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class NatsConnection {
  public static final String CHANNEL_SEPARATOR = ".";
  private static final Logger log = LoggerFactory.getLogger(NatsConnection.class.getName());

  private NatsConnection() {}

  public static Connection create(YamlConfigMap c) throws IOException, InterruptedException {
    var server = c.asString("server");
    var conTimeout = Duration.ofMillis(c.asLong("connectionTimeout"));
    var reconTimeout = Duration.ofMillis(c.asLong("reconnectionTimeout"));
    var pingInterval = Duration.ofMillis(c.asLong("pingInterval"));
    var bufferSize = c.asInteger("bufferSize");

    var config = new Options.Builder().server(server)
                                      .connectionTimeout(conTimeout)
                                      .reconnectWait(reconTimeout)
                                      .bufferSize(bufferSize)
                                      .pingInterval(pingInterval)
                                      .connectionListener((conn, type) -> log.info("Status change " + type))
                                      .errorListener(err)
                                      .build();
    return Nats.connect(config);
  }

  private static ErrorListener err = new ErrorListener() {
    public void exceptionOccurred(Connection conn, Exception exp) {
      log.error("Exception " + exp.getMessage());
    }

    public void errorOccurred(Connection conn, String type) {
      log.error("Error " + type);
    }

    public void slowConsumerDetected(Connection conn, Consumer consumer) {
      log.error("Slow consumer on connection:" + conn.getConnectedUrl());
    }
  };
}
