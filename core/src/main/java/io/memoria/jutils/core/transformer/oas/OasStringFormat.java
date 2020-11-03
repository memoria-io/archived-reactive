package io.memoria.jutils.core.transformer.oas;

public record OasStringFormat(String value) {
  /**
   * Standard, full-date notation as defined by RFC 3339, section 5.6, for example, 2017-07-21
   */
  public static final OasStringFormat DATE = new OasStringFormat("date");

  /**
   * Standard, date-time notation as defined by RFC 3339, section 5.6, for example, 2017-07-21T17:32:28Z
   */
  public static final OasStringFormat DATE_TIME = new OasStringFormat("date-time");

  /**
   * Standard, password – a hint to UIs to mask the input
   */
  public static final OasStringFormat PASSWORD = new OasStringFormat("password");

  /**
   * Standard, byte – base64-encoded characters, for example, U3dhZ2dlciByb2Nrcw==
   */
  public static final OasStringFormat BYTE = new OasStringFormat("byte");

  /**
   * Standard, binary – binary data, used to describe files (see Files below)
   */
  public static final OasStringFormat BINARY = new OasStringFormat("binary");

  /**
   * NON Standard
   */
  public static final OasStringFormat EMAIL = new OasStringFormat("email");

  /**
   * NON Standard
   */
  public static final OasStringFormat UUID = new OasStringFormat("uuid");

  /**
   * NON Standard
   */
  public static final OasStringFormat URI = new OasStringFormat("uri");

  /**
   * NON Standard
   */
  public static final OasStringFormat hostname = new OasStringFormat("hostname");

  /**
   * NON Standard
   */
  public static final OasStringFormat IPV4 = new OasStringFormat("ipv4");
}
