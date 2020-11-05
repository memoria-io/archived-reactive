package io.memoria.jutils.core.transformer.lcd;

import java.util.Map;

/**
 * Least Common Denominator Pojo
 * 
 */
public sealed interface LCD {
  record LCDArray(LCD type) implements LCD {}

  record LCDBoolean() implements LCD {}

  record LCDDouble() implements LCD {}

  record LCDFloat() implements LCD {}

  record LCDInteger() implements LCD {}

  record LCDLong() implements LCD {}

  /**
   * Key type is string in JSON
   */
  record LCDMap(LCD valuesType) implements LCD {}

  record LCDObject(Map<String, LCD> properties) implements LCD {}

  record LCDString() implements LCD {}
}
