package com.marmoush.jutils.http.netty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Function;

import static com.marmoush.jutils.functional.Functional.instanceOfCase;
import static com.marmoush.jutils.http.netty.NettyHttpErrorAdapter.nettyHttpErrorCase;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class NettyHttpErrorAdapterTest {
  @Test
  public void adaptTest() {
    Function<Throwable, NettyHttpError> mapping = t -> Match(t).of(nettyHttpErrorCase(CreationError.class, t, 400),
                                                                   Case($(instanceOf(SavingError.class)),
                                                                        () -> new NettyHttpError(t, 400)),
                                                                   instanceOfCase(LoginError.class,
                                                                                  new NettyHttpError(t, 403)),
                                                                   nettyHttpErrorCase(IllegalArgumentException.class,
                                                                                      t,
                                                                                      400),
                                                                   Case($(), () -> new NettyHttpError(t, 500)));
    NettyHttpErrorAdapter adapter = new NettyHttpErrorAdapter(mapping);
    Assertions.assertEquals(400, adapter.apply(new CreationError()).statusCode);
    Assertions.assertEquals(400, adapter.apply(new SavingError()).statusCode);
    Assertions.assertEquals(403, adapter.apply(new LoginError()).statusCode);
    Assertions.assertEquals(400, adapter.apply(new IllegalArgumentException()).statusCode);
    Assertions.assertEquals(500, adapter.apply(new IOException()).statusCode);
  }

  private static class CreationError extends Throwable {
    public CreationError() {
      super("Couldn't Crate");
    }
  }

  private static class SavingError extends Throwable {}

  private static class LoginError extends Throwable {}
}
