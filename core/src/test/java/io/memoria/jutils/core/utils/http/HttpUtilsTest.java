package io.memoria.jutils.core.utils.http;

import io.vavr.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.memoria.jutils.core.utils.http.HttpUtils.basicCredentials;
import static io.memoria.jutils.core.utils.http.HttpUtils.bearerToken;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class HttpUtilsTest {

  @Test
  public void basicExtraSpacesFail() {
    var header = "Basic  " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> basicCredentials(header));
  }

  @Test
  public void basicExtraSpacesSuccess() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes()) + "   ";
    var t = basicCredentials(header);
    Assertions.assertEquals(Tuple.of("bob", "password"), t.get());
  }

  @Test
  public void basicNoColonFail() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> basicCredentials(header));
  }

  @Test
  public void basicNoIdSuccess() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("" + ":" + "password").getBytes()) + "   ";
    var t = basicCredentials(header);
    Assertions.assertEquals(Tuple.of("", "password"), t.get());
  }

  @Test
  public void basicNoPasswordFail() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> basicCredentials(header));
  }

  @Test
  public void basicSuccess() {
    var header = "Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    var t = basicCredentials(header);
    Assertions.assertEquals(Tuple.of("bob", "password"), t.get());
  }

  @Test
  public void bearerSuccess() {
    var token = "xyz.xyz.zyz";
    var header = "Bearer " + token;
    Assertions.assertEquals(some(token), bearerToken(header));
  }

  @Test
  public void noBasic() {
    var header = "   Base " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    Assertions.assertEquals(none(), basicCredentials(header));
  }

  @Test
  public void noBearer() {
    var token = "xyz.xyz.zyz";
    var header = "Bearr " + token;
    Assertions.assertEquals(none(), bearerToken(header));
  }
}
