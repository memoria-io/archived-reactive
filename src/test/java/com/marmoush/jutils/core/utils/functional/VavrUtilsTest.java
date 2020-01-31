package com.marmoush.jutils.core.utils.functional;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;

public class VavrUtilsTest {
  private Try<List<Integer>> success = Try.of(() -> List.of(1, 2, 3));
  private Exception e = new Exception();
  private Try<List<Integer>> failure = Try.failure(e);

  @Test
  public void traverseOfTryTest() {
    List<Try<Integer>> su = List.ofAll(VavrUtils.traverseOfTry(success));
    List<Try<Integer>> fa = List.ofAll(VavrUtils.traverseOfTry(failure));
    Assertions.assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    Assertions.assertEquals(List.of(Try.failure(e)), fa);
  }

  @Test
  void listOfTryTest() {
    List<Try<Integer>> su = List.ofAll(VavrUtils.listOfTry(success));
    List<Try<Integer>> fa = List.ofAll(VavrUtils.listOfTry(failure));
    Assertions.assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    Assertions.assertEquals(List.of(Try.failure(e)), fa);
  }
}
