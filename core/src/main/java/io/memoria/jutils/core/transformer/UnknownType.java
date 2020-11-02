package io.memoria.jutils.core.transformer;

import java.io.IOException;

public class UnknownType extends IOException {
  public UnknownType(String msg) {
    super(msg);
  }
}
