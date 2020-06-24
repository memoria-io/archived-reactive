package io.memoria.jutils.core.utils.http;

import io.vavr.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class HttpUtilsTest {

  @Test
  public void basicExtraSpacesFail() {
    String header = "Basic  " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.toBasicAuthCredentials(header));
  }

  @Test
  public void basicExtraSpacesSuccess() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes()) + "   ";
    var t = HttpUtils.toBasicAuthCredentials(header);
    Assertions.assertEquals(Tuple.of("bob", "password"), t);
  }

  @Test
  public void basicNoColonFail() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.toBasicAuthCredentials(header));
  }

  @Test
  public void basicNoIdSuccess() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("" + ":" + "password").getBytes()) + "   ";
    var t = HttpUtils.toBasicAuthCredentials(header);
    Assertions.assertEquals(Tuple.of("", "password"), t);
  }

  @Test
  public void basicNoPasswordFail() {
    String header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.toBasicAuthCredentials(header));
  }

  @Test
  public void basicSuccess() {
    String header = "Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    var t = HttpUtils.toBasicAuthCredentials(header);
    Assertions.assertEquals(Tuple.of("bob", "password"), t);
  }
}
