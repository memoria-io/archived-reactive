package io.memoria.jutils.jackson.transformer.oas;

/**
 * <ul>
 *   <li>date-time notation as defined by RFC 3339, section 5.6, for example, 2017-07-21T17:32:28Z</li>
 *  <li>full-date notation as defined by RFC 3339, section 5.6, for example, 2017-07-21</li>
 *  <li>password notation is a hint to UIs to mask the input</li>
 *  <li>base64-encoded characters, for example, U3dhZ2dlciByb2Nrcw==</li>
 *  <li>byte</li>
 *  <li>binary</li>
 * </ul>
 */
public enum OAS3StringFormat {
  DATE("date"),
  DATE_TIME("date-time"),
  PASSWORD("password"),
  BYTE("byte"),
  BINARY("binary");
  
  public final String value;

  OAS3StringFormat(String value) {
    this.value = value;
  }
}
