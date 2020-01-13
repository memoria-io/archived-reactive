package com.marmoush.jutils.adapter.msgbus.Nats;

import io.nats.client.*;
import io.vavr.collection.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class NatsConnection {
  public static final String CHANNEL_SEPARATOR = ".";
  private static final Logger log = LoggerFactory.getLogger(NatsConnection.class.getName());

  private NatsConnection() {}

  public static Connection create(Map<String, Object> configMap) throws IOException, InterruptedException {
    var configs = configMap.mapValues(v -> (String) v);
    var conTimeout = Duration.ofMillis(Long.parseLong(configs.get("connectionTimeout").get()));
    var reconTimeout = Duration.ofMillis(Long.parseLong(configs.get("reconnectionTimeout").get()));
    var pingInterval = Duration.ofMillis(Long.parseLong(configs.get("pingInterval").get()));
    var bufferSize = Integer.parseInt(configs.get("bufferSize").get());

    var config = new Options.Builder().server(configs.get("server").get())
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
