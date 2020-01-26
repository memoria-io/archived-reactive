package com.marmoush.jutils;

import org.junit.jupiter.api.*;
import org.slf4j.*;

public class LogLevelsTest {
  private static final Logger log = LoggerFactory.getLogger(LogLevelsTest.class.getName());

  @Test
  public void testLogLevels() {
    // This is currently just a smoke test, later will use log files.
    log.trace("Trace message !");
    log.debug("Debug message !");
    log.info("Info message !");
    log.warn("Warn message !");
    log.error("Error message !");
    Assertions.assertFalse(log.isTraceEnabled());
    Assertions.assertTrue(log.isInfoEnabled());
    Assertions.assertTrue(log.isWarnEnabled());
    Assertions.assertTrue(log.isErrorEnabled());
  }
}
