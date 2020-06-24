package io.memoria.jutils.core.utils.http;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.Base64;

public final class HttpUtils {
  public static Tuple2<String, String> toBasicAuthCredentials(String header) {
    String content = header.trim().split(" ")[1].trim();
    String[] basic = new String(Base64.getDecoder().decode(content)).split(":");
    return Tuple.of(basic[0], basic[1]);
  }

  private HttpUtils() {}
}
