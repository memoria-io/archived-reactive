package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.EvolverTest.AccountEvent.BalanceAdded;
import io.memoria.jutils.core.eventsourcing.event.EvolverTest.AccountEvent.BalanceWithdrawn;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class EvolverTest {
  interface AccountEvent extends Event {
    record BalanceAdded(String id, String aggId, int value) implements AccountEvent {}

    record BalanceWithdrawn(String id, String aggId, int value) implements AccountEvent {}
  }

  private static record Account(String id, int balance) implements State {}

  private static record AccountEvolver() implements Evolver<Account> {
    @Override
    public Account apply(Account account, Event accountEvent) {
      if (accountEvent instanceof BalanceAdded balanceAdded) {
        return new Account(account.id, account.balance + balanceAdded.value);
      }

      if (accountEvent instanceof BalanceWithdrawn balanceWithdrawn) {
        return new Account(account.id, account.balance - balanceWithdrawn.value);
      }
      return account;
    }
  }

  private final Evolver<Account> e = new AccountEvolver();

  @Test
  public void apply() {
    var acc = new Account("0", 10);
    var newAcc = e.apply(acc, new BalanceAdded("0", "0", 10));
    Assertions.assertEquals(20, newAcc.balance);
  }

  @Test
  public void applyCurriedFlux() {
    var acc = new Account("0", 10);
    var newAcc = e.curriedFlux(acc).apply(Flux.just(new BalanceAdded("0", "0", 10), new BalanceAdded("0", "0", 10)));
    StepVerifier.create(newAcc.map(Account::balance)).expectNext(30).expectComplete().verify();
  }

  @Test
  public void applyCurriedTraversal() {
    var acc = new Account("0", 10);
    var newAcc = e.curriedTraversable(acc)
                  .apply(List.of(new BalanceAdded("0", "0", 10), new BalanceAdded("0", "0", 10)));
    Assertions.assertEquals(30, newAcc.balance);
  }

  @Test
  public void applyFlux() {
    var acc = new Account("0", 10);
    var newAcc = e.apply(acc, Flux.just(new BalanceAdded("0", "0", 10), new BalanceAdded("0", "0", 10)));
    StepVerifier.create(newAcc.map(Account::balance)).expectNext(30).expectComplete().verify();
  }

  @Test
  public void applyTraversal() {
    var acc = new Account("0", 10);
    var newAcc = e.apply(acc, List.of(new BalanceAdded("0", "0", 10), new BalanceAdded("0", "0", 10)));
    Assertions.assertEquals(30, newAcc.balance);
  }
}
