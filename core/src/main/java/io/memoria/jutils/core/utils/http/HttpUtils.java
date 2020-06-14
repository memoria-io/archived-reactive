package io.memoria.jutils.core.utils.http;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;

import java.util.Base64;

public final class HttpUtils {
  private HttpUtils() {}

  public static Try<Tuple2<String, String>> basicAuth(String header) {
    return Try.of(() -> {
      String content = header.trim().split(" ")[1].trim();
      String[] basic = new String(Base64.getDecoder().decode(content)).split(":");
      return Tuple.of(basic[0], basic[1]);
    });
  }
}
