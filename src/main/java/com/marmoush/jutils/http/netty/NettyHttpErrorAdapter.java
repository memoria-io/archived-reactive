package com.marmoush.jutils.http.netty;

import java.util.function.Function;

import static com.marmoush.jutils.functional.Functional.instanceOfCase;
import static io.vavr.API.Match;

public class NettyHttpErrorAdapter implements Function<Throwable, NettyHttpError> {
  private final Function<Throwable, NettyHttpError> f;
  //  private final Function<Throwable, Match.Case<? extends Throwable, NettyHttpError>> []cases;
  //
  //  public NettyHttpErrorAdapter(Function<Throwable, Match.Case<? extends Throwable, NettyHttpError>>[] cases) {
  //    this.cases = cases;
  //  }

  public NettyHttpErrorAdapter(Function<Throwable, NettyHttpError> f) {
    this.f = f;
  }

  //  public static <T extends Throwable> Match.Case<T, NettyHttpError> nettyErrorCase(Function<Throwable, Class<T> c,
  //                                                                                   Throwable t,
  //                                                                                   int status) {
  //    return instanceOfCase(c, new NettyHttpError(t, status));
  //  }

  @Override
  public NettyHttpError apply(Throwable throwable) {
    return f.apply(throwable);
  }

  public static <T extends Throwable> Match.Case<T, NettyHttpError> nettyHttpErrorCase(Class<?> c, T t, int status) {
    return instanceOfCase(c, new NettyHttpError(t, status));
  }
}
