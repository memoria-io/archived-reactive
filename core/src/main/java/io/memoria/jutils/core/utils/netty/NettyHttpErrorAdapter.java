package io.memoria.jutils.core.utils.netty;

import io.memoria.jutils.core.utils.functional.VavrUtils;

import java.util.function.Function;

import static io.vavr.API.Match;

public record NettyHttpErrorAdapter(Function<Throwable, NettyHttpError>f)
        implements Function<Throwable, NettyHttpError> {
  public static <T extends Throwable> Match.Case<T, NettyHttpError> nettyHttpErrorCase(Class<?> c, T t, int status) {
    return VavrUtils.instanceOfCase(c, new NettyHttpError(t, status));
  }

  @Override
  public NettyHttpError apply(Throwable throwable) {
    return f.apply(throwable);
  }
}
