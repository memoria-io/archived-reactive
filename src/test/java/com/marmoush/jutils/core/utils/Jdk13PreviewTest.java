package com.marmoush.jutils.core.utils;

import org.junit.jupiter.api.*;

public class Jdk13PreviewTest {
  public static int switchJdk13(String mode) {
    return switch (mode) {
      case "a", "b" -> 1;
      case "c" -> 2;
      case "d", "e", "f" -> 3;
      default -> -1;
    };
  }

  @Test
  void switchTest() {
    Assertions.assertEquals(1, switchJdk13("a"));
    Assertions.assertEquals(2, switchJdk13("c"));
    Assertions.assertEquals(-1, switchJdk13("z"));
  }
}
