package io.memoria.jutils.security.adapter.argon;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.jutils.security.domain.port.Verifier;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public class Argon2Verifier implements Verifier {
  private final Argon2 argon2;
  private final Scheduler scheduler;

  public Argon2Verifier(Scheduler scheduler) {
    this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    this.scheduler = scheduler;
  }

  @Override
  public Mono<Boolean> verify(String password, String hash, String salt) {
    return Mono.defer(() -> Mono.just(blockingVerify(password, hash, salt)).subscribeOn(scheduler));
  }

  public boolean blockingVerify(String password, String hash, String salt) {
    boolean verify = argon2.verify(hash, (password + salt).getBytes());
    argon2.wipeArray(password.toCharArray());
    return verify;
  }
}
