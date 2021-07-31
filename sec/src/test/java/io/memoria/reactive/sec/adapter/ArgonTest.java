package io.memoria.reactive.sec.adapter;

import io.memoria.reactive.core.text.TextGenerator;
import io.memoria.reactive.sec.Hasher;
import io.memoria.reactive.sec.Verifier;
import io.memoria.reactive.sec.adapter.argon.ArgonHasher;
import io.memoria.reactive.sec.adapter.argon.ArgonVerifier;
import io.vavr.collection.Stream;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.security.SecureRandom;

class ArgonTest {
  private final Hasher hasher = new ArgonHasher(100, 1024, 4, Schedulers.boundedElastic());
  private final Verifier verifier = new ArgonVerifier(Schedulers.boundedElastic());

  @Test
  void hashAndVerifyTest() {
    SecureRandom secRand = new SecureRandom();
    var text = new TextGenerator(secRand);
    Stream.range(0, 10).forEach(t -> {
      int min = secRand.nextInt(20);
      int max = min + 200;
      String password = text.minMaxAlphanumeric(min, max);
      String salt = text.minMaxAlphanumeric(min, max);
      var m = hasher.hash(password, salt).flatMap(hash -> verifier.verify(password, hash, salt));
      StepVerifier.create(m).expectNext(true).expectComplete().verify();
    });
  }
}
