package com.marmoush.jutils.http.netty;

import java.util.function.Function;

import static com.marmoush.jutils.functional.Functional.instanceOfCase;
import static io.vavr.API.Match;

public class NettyHttpErrorAdapter implements Function<Throwable, NettyHttpError> {
  private final Function<Throwable, NettyHttpError> f;

  public NettyHttpErrorAdapter(Function<Throwable, NettyHttpError> f) {
    this.f = f;
  }

  @Override
  public NettyHttpError apply(Throwable throwable) {
    return f.apply(throwable);
  }

  public static <T extends Throwable> Match.Case<T, NettyHttpError> nettyHttpErrorCase(Class<?> c, T t, int status) {
    return instanceOfCase(c, new NettyHttpError(t, status));
  }
}
