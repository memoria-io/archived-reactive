package com.marmoush.jutils.core;

import org.junit.jupiter.api.*;

public class Jdk13PreviewTest {
  @Test
  void test13Preview() {
    String mode = "d";
    int result = switch (mode) {
      case "a", "b" -> 1;
      case "c" -> 2;
      case "d", "e", "f" -> 3;
      default -> -1;
    };
    Assertions.assertEquals(3, result);
  }
}
