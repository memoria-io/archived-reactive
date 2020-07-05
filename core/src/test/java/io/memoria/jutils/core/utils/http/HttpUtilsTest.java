package io.memoria.jutils.core.utils.http;

import io.vavr.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class HttpUtilsTest {

  @Test
  public void basicExtraSpacesFail() {
    var header = "Basic  " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.basicFrom(header));
  }

  @Test
  public void basicExtraSpacesSuccess() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes()) + "   ";
    var t = HttpUtils.basicFrom(header);
    Assertions.assertEquals(Tuple.of("bob", "password"), t.get());
  }

  @Test
  public void basicNoColonFail() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.basicFrom(header));
  }

  @Test
  public void basicNoIdSuccess() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("" + ":" + "password").getBytes()) + "   ";
    var t = HttpUtils.basicFrom(header);
    Assertions.assertEquals(Tuple.of("", "password"), t.get());
  }

  @Test
  public void basicNoPasswordFail() {
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "").getBytes()) + "   ";
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> HttpUtils.basicFrom(header));
  }

  @Test
  public void basicSuccess() {
    var header = "Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());
    var t = HttpUtils.basicFrom(header);
    Assertions.assertEquals(Tuple.of("bob", "password"), t.get());
  }

  @Test
  public void bearerSuccess() {
    var token = "xyz.xyz.zyz";
    var header = "Bearer " + token;
    Assertions.assertEquals(some(token), HttpUtils.tokenFrom(header));
  }

  @Test
  public void noBasic() {
    var header = "   Base " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    Assertions.assertEquals(none(), HttpUtils.basicFrom(header));
  }

  @Test
  public void noBearer() {
    var token = "xyz.xyz.zyz";
    var header = "Bearr " + token;
    Assertions.assertEquals(none(), HttpUtils.tokenFrom(header));
  }
}
