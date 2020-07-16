package io.memoria.jutils.core.http;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;

import java.util.Base64;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public final class HttpUtils {

  public static Option<Tuple2<String, String>> basicFrom(String header) {
    header = header.trim();
    if (header.contains("Basic")) {
      String content = header.split(" ")[1].trim();
      String[] basic = new String(Base64.getDecoder().decode(content)).split(":");
      return some(Tuple.of(basic[0], basic[1]));
    } else {
      return none();
    }
  }

  public static Option<String> tokenFrom(String header) {
    header = header.trim();
    if (header.contains("Bearer")) {
      return some(header.split(" ")[1].trim());
    } else {
      return none();
    }
  }

  private HttpUtils() {}
}
