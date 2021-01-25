package io.memoria.jutils.jvavr;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VavrUtilsTest {
  private final Try<List<Integer>> success = Try.of(() -> List.of(1, 2, 3));
  private final Exception e = new Exception();
  private final Try<List<Integer>> failure = Try.failure(e);

  @Test
  void handleTest() throws ExecutionException, InterruptedException {
    var success = CompletableFuture.completedFuture("success");
    assertEquals(Try.success("success"), success.handle(VavrUtils.handle()).get());

    var e = new Exception("failure");
    var failure = CompletableFuture.failedFuture(e);
    assertEquals(Try.failure(e), failure.handle(VavrUtils.handle()).get());
  }

  @Test
  void handleToVoidTest() throws ExecutionException, InterruptedException {
    var success = CompletableFuture.completedFuture("success");
    assertEquals(Try.success(null), success.handle(VavrUtils.handleToVoid()).get());

    var e = new Exception("failure");
    var failure = CompletableFuture.failedFuture(e);
    assertEquals(Try.failure(e), failure.handle(VavrUtils.handleToVoid()).get());
  }


  @Test
  void listOfTryTest() {
    List<Try<Integer>> su = List.ofAll(VavrUtils.listOfTry(success));
    List<Try<Integer>> fa = List.ofAll(VavrUtils.listOfTry(failure));
    assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    assertEquals(List.of(Try.failure(e)), fa);
  }

  @Test
  void traverseOfTryTest() {
    List<Try<Integer>> su = List.ofAll(VavrUtils.traverseOfTry(success));
    List<Try<Integer>> fa = List.ofAll(VavrUtils.traverseOfTry(failure));
    assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    assertEquals(List.of(Try.failure(e)), fa);
  }

}
