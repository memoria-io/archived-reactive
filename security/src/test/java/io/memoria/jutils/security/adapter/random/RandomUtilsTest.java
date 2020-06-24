package io.memoria.jutils.security.adapter.random;

import io.vavr.collection.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

public class RandomUtilsTest {
  @Test
  public void randomAlphanumericTest() {
    SecureRandom secRand = new SecureRandom();
    RandomUtils ru = new RandomUtils(secRand);
    Stream.range(0, 100).forEach(t -> {
      int min = secRand.nextInt(10);
      int max = min + 200;
      Assertions.assertEquals(ru.randomAlphanumeric(max).length(), max);
      Assertions.assertTrue(ru.randomMinMaxAlphanumeric(min, max).length() <= max);
      Assertions.assertTrue(ru.randomMinMaxAlphanumeric(min, max).length() >= min);
    });
  }

  @Test
  public void randomHexTest() {
    SecureRandom secRand = new SecureRandom();
    RandomUtils ru = new RandomUtils(secRand);
    Stream.range(0, 100).forEach(t -> {
      int min = secRand.nextInt(10);
      int max = min + 200;
      Assertions.assertEquals(ru.randomHex(max).length(), max);
      Assertions.assertTrue(ru.randomMinMaxHex(min, max).length() <= max);
      Assertions.assertTrue(ru.randomMinMaxHex(min, max).length() >= min);
      Assertions.assertFalse(ru.randomMinMaxHex(min, max).contains("g") || ru.randomMinMaxHex(min, max).contains("h"));
    });
  }
}
