package io.memoria.jutils.core.utils.http;

import io.vavr.Tuple;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class HttpUtilsTest {

  @Test
  public void basicExtraSpacesFail() {
    String header = "Basic  " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.basicAuth(header).get());
  }

  @Test
  public void basicExtraSpacesSuccess() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes()) + "   ";
    var t = HttpUtils.basicAuth(header);
    Assertions.assertEquals(Try.success(Tuple.of("bob", "password")), t);
  }

  @Test
  public void basicNoColonFail() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.basicAuth(header).get());
  }

  @Test
  public void basicNoIdSuccess() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("" + ":" + "password").getBytes()) + "   ";
    var t = HttpUtils.basicAuth(header);
    Assertions.assertEquals(Try.success(Tuple.of("", "password")), t);
  }

  @Test
  public void basicNoPasswordFail() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.basicAuth(header).get());
  }

  @Test
  public void basicSuccess() {
    String header = "Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    var t = HttpUtils.basicAuth(header);
    Assertions.assertEquals(Try.success(Tuple.of("bob", "password")), t);
  }
}
