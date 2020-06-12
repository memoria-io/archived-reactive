package io.memoria.jutils.security.adapter.argon;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.jutils.security.domain.port.Hasher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public class Argon2Hasher implements Hasher {
  private final Argon2 argon2;
  private final int iterations;
  private final int memory;
  private final int parallelism;
  private final Scheduler scheduler;

  public Argon2Hasher(int iterations, int memory, int parallelism, Scheduler scheduler) {
    argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    this.iterations = iterations;
    this.memory = memory;
    this.parallelism = parallelism;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<String> hash(String password, String salt) {
    return Mono.defer(() -> Mono.just(blockingHash(password, salt)).subscribeOn(scheduler));
  }

  @Override
  public String blockingHash(String password, String salt) {
    String saltedPass = password + salt;
    String hash = argon2.hash(iterations, memory, parallelism, saltedPass.getBytes());
    argon2.wipeArray(saltedPass.toCharArray());
    return hash;
  }
}
