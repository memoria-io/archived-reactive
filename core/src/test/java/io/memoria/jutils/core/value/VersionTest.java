package io.memoria.jutils.core.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionTest {
  @Test
  @DisplayName("Should produce exception")
  void invalidVersion() {
    Assertions.assertTrue(Version.from("23..2").getCause() instanceof IllegalArgumentException);
    Assertions.assertTrue(Version.from("23.a.2").getCause() instanceof NumberFormatException);
  }

  @Test
  @DisplayName("Should produce correct version")
  void validVersion() {
    Assertions.assertEquals(Version.from("1.2.3").get(), new Version(1, 2, 3));
  }
}
