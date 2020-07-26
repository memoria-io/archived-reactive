package io.memoria.jutils.adapter.functional;

import io.memoria.jutils.core.Err.NotFound;
import io.memoria.jutils.core.functional.VavrUtils;
import io.vavr.API.Match.Case;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.memoria.jutils.core.functional.VavrUtils.instanceOfCase;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.vavr.API.Match;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VavrUtilsTest {
  private final Try<List<Integer>> success = Try.of(() -> List.of(1, 2, 3));
  private final Exception e = new Exception();
  private final Try<List<Integer>> failure = Try.failure(e);

  @Test
  public void handleTest() throws ExecutionException, InterruptedException {
    var success = CompletableFuture.completedFuture("success");
    assertEquals(Try.success("success"), success.handle(VavrUtils.handle()).get());

    var e = new Exception("failure");
    var failure = CompletableFuture.failedFuture(e);
    assertEquals(Try.failure(e), failure.handle(VavrUtils.handle()).get());
  }

  @Test
  public void handleToVoidTest() throws ExecutionException, InterruptedException {
    var success = CompletableFuture.completedFuture("success");
    assertEquals(Try.success(null), success.handle(VavrUtils.handleToVoid()).get());

    var e = new Exception("failure");
    var failure = CompletableFuture.failedFuture(e);
    assertEquals(Try.failure(e), failure.handle(VavrUtils.handleToVoid()).get());
  }

  @Test
  public void instanceOfCaseTest() {
    Case<Throwable, String> case1 = instanceOfCase(IllegalArgumentException.class, BAD_REQUEST.reasonPhrase());
    Case<Throwable, String> case2 = instanceOfCase(NotFound.class, BAD_REQUEST.reasonPhrase());
    Function1<Throwable, String> f = t -> Match(t).of(case1, case2);
    Assertions.assertEquals(f.apply(new IllegalArgumentException()), BAD_REQUEST.reasonPhrase());
  }

  @Test
  public void listOfTryTest() {
    List<Try<Integer>> su = List.ofAll(VavrUtils.listOfTry(success));
    List<Try<Integer>> fa = List.ofAll(VavrUtils.listOfTry(failure));
    assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    assertEquals(List.of(Try.failure(e)), fa);
  }

  @Test
  public void traverseOfTryTest() {
    List<Try<Integer>> su = List.ofAll(VavrUtils.traverseOfTry(success));
    List<Try<Integer>> fa = List.ofAll(VavrUtils.traverseOfTry(failure));
    assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    assertEquals(List.of(Try.failure(e)), fa);
  }

}
