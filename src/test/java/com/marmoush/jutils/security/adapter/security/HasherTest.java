package com.marmoush.jutils.security.adapter.security;

import com.marmoush.jutils.security.domain.port.Hasher;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

public class HasherTest {
  Hasher hasher = new Argon2Hasher(100, 1024, 4);

  @Test
  public void hashAndVerifyTest() {
    SecureRandom secRand = new SecureRandom();
    RandomUtils ru = new RandomUtils(secRand);
    Stream.range(0, 10).forEach(t -> {
      int min = secRand.nextInt(20);
      int max = min + 200;
      String password = ru.randomMinMaxAlphanumeric(min, max);
      String salt = ru.randomMinMaxAlphanumeric(min, max);
      String hash = hasher.hash(password, salt);
      Assertions.assertTrue(hasher.verify(password, hash, salt));
    });
  }
}
