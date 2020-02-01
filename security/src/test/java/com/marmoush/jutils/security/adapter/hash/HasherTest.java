package com.marmoush.jutils.security.adapter.hash;

import com.marmoush.jutils.security.adapter.random.RandomUtils;
import com.marmoush.jutils.security.domain.port.Hasher;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.*;
import reactor.core.scheduler.*;
import reactor.test.StepVerifier;

import java.security.SecureRandom;

public class HasherTest {
  Hasher hasher = new Argon2Hasher(100, 1024, 4, Schedulers.elastic());

  @Test
  public void hashAndVerifyTest() {
    SecureRandom secRand = new SecureRandom();
    RandomUtils ru = new RandomUtils(secRand);
    Stream.range(0, 10).forEach(t -> {
      int min = secRand.nextInt(20);
      int max = min + 200;
      String password = ru.randomMinMaxAlphanumeric(min, max);
      String salt = ru.randomMinMaxAlphanumeric(min, max);
      var m = hasher.hash(password, salt).flatMap(hash -> hasher.verify(password, hash, salt));
      StepVerifier.create(m).expectNext(true).expectComplete().verify();
    });
  }
}
