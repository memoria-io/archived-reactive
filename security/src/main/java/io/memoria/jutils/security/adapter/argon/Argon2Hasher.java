package io.memoria.jutils.security.adapter.argon;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.jutils.security.domain.port.Hasher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Objects;

public class Argon2Hasher implements Hasher {
  private final Argon2 argon2;
  private final int iterations;
  private final int memory;
  private final int parallelism;
  private final Scheduler scheduler;

  public Argon2Hasher(int iterations, int memory, int parallelism, Scheduler scheduler) {
    this(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id), iterations, memory, parallelism, scheduler);
  }

  public Argon2Hasher(Argon2 argon2, int iterations, int memory, int parallelism, Scheduler scheduler) {
    this.argon2 = argon2;
    this.iterations = iterations;
    this.memory = memory;
    this.parallelism = parallelism;
    this.scheduler = scheduler;
  }

  @Override
  public String blockingHash(String password, String salt) {
    String saltedPass = password + salt;
    String hash = argon2.hash(iterations, memory, parallelism, saltedPass.getBytes());
    argon2.wipeArray(saltedPass.toCharArray());
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Argon2Hasher that = (Argon2Hasher) o;
    return iterations == that.iterations && memory == that.memory && parallelism == that.parallelism &&
           argon2.equals(that.argon2) && scheduler.equals(that.scheduler);
  }

  @Override
  public Mono<String> hash(String password, String salt) {
    return Mono.defer(() -> Mono.just(blockingHash(password, salt)).subscribeOn(scheduler));
  }

  @Override
  public int hashCode() {
    return Objects.hash(argon2, iterations, memory, parallelism, scheduler);
  }
}
