package com.marmoush.jutils.adapter.msgbus.Nats;

import io.nats.client.*;
import io.vavr.collection.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class NatsConnection {
  private static final Logger log = LoggerFactory.getLogger(NatsConnection.class.getName());

  private NatsConnection() {}

  public static Connection create(Map<String, Object> configs) throws IOException, InterruptedException {
    var config = new Options.Builder().server((String) configs.get("server").get())
                                      .connectionTimeout(Duration.ofMillis((Long) configs.get("connectionTimeout")
                                                                                         .get()))
                                      .reconnectWait(Duration.ofMillis((Long) configs.get("reconnectionTimeout").get()))
                                      .bufferSize((Integer) configs.get("bufferSize").get())
                                      .pingInterval(Duration.ofMillis((Long) configs.get("pingInterval").get()))
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
