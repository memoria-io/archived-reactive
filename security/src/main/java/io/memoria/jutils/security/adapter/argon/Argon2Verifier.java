package io.memoria.jutils.security.adapter.argon;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.jutils.security.domain.port.Verifier;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Objects;

public class Argon2Verifier implements Verifier {
  private final Argon2 argon2;
  private final Scheduler scheduler;

  public Argon2Verifier(Scheduler scheduler) {
    this(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id), scheduler);
  }

  public Argon2Verifier(Argon2 argon2, Scheduler scheduler) {
    this.argon2 = argon2;
    this.scheduler = scheduler;
  }

  public boolean blockingVerify(String password, String hash, String salt) {
    boolean verify = argon2.verify(hash, (password + salt).getBytes());
    argon2.wipeArray(password.toCharArray());
    return verify;
  }

  @Override
  public Mono<Boolean> verify(String password, String hash, String salt) {
    return Mono.defer(() -> Mono.just(blockingVerify(password, hash, salt)).subscribeOn(scheduler));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Argon2Verifier that = (Argon2Verifier) o;
    return argon2.equals(that.argon2) && scheduler.equals(that.scheduler);
  }

  @Override
  public int hashCode() {
    return Objects.hash(argon2, scheduler);
  }
}
