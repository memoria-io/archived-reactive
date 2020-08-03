package io.memoria.jutils.security.adapter.hasher.argon;

import io.memoria.jutils.adapter.generator.RandomStringGenerator;
import io.memoria.jutils.core.generator.StringGenerator;
import io.memoria.jutils.security.Hasher;
import io.memoria.jutils.security.Verifier;
import io.memoria.jutils.security.adapter.verifier.argon.ArgonVerifier;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.security.SecureRandom;

public class ArgonTest {
  private final Hasher hasher = new ArgonHasher(100, 1024, 4, Schedulers.elastic());
  private final Verifier verifier = new ArgonVerifier(Schedulers.elastic());

  @Test
  public void hashAndVerifyTest() {
    SecureRandom secRand = new SecureRandom();
    StringGenerator ru = new RandomStringGenerator(secRand);
    Stream.range(0, 10).forEach(t -> {
      int min = secRand.nextInt(20);
      int max = min + 200;
      String password = ru.minMaxAlphanumeric(min, max);
      String salt = ru.minMaxAlphanumeric(min, max);
      var m = hasher.hash(password, salt).flatMap(hash -> verifier.verify(password, hash, salt));
      StepVerifier.create(m).expectNext(true).expectComplete().verify();
    });
  }
}
