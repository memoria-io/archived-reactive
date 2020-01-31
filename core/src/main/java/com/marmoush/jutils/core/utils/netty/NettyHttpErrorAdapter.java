package com.marmoush.jutils.core.utils.netty;

import com.marmoush.jutils.core.utils.functional.VavrUtils;

import java.util.function.Function;

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
    return VavrUtils.instanceOfCase(c, new NettyHttpError(t, status));
  }
}
