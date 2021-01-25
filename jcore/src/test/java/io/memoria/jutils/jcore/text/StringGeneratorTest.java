package io.memoria.jutils.jcore.text;

import io.memoria.jutils.jcore.text.TextGenerator;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

class StringGeneratorTest {
  @Test
  void randomAlphanumericTest() {
    SecureRandom secRand = new SecureRandom();
    TextGenerator ru = new TextGenerator(secRand);
    Stream.range(0, 100).forEach(t -> {
      int min = secRand.nextInt(10);
      int max = min + 200;
      Assertions.assertEquals(ru.alphanumeric(max).length(), max);
      Assertions.assertTrue(ru.minMaxAlphanumeric(min, max).length() <= max);
      Assertions.assertTrue(ru.minMaxAlphanumeric(min, max).length() >= min);
    });
  }

  @Test
  void randomHexTest() {
    SecureRandom secRand = new SecureRandom();
    TextGenerator ru = new TextGenerator(secRand);
    Stream.range(0, 100).forEach(t -> {
      int min = secRand.nextInt(10);
      int max = min + 200;
      Assertions.assertEquals(ru.hex(max).length(), max);
      Assertions.assertTrue(ru.minMaxHex(min, max).length() <= max);
      Assertions.assertTrue(ru.minMaxHex(min, max).length() >= min);
      Assertions.assertFalse(ru.minMaxHex(min, max).contains("g") || ru.minMaxHex(min, max).contains("h"));
    });
  }
}
