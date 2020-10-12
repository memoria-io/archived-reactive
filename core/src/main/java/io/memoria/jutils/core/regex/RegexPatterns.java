package io.memoria.jutils.core.regex;

import java.util.regex.Pattern;

public class RegexPatterns {
  public static final Pattern ALL;
  public static final Pattern BASE_64;
  public static final Pattern RFC_3339_DATE;
  public static final Pattern rfc3339_time;
  public static final Pattern RFC_3339_DATETIME;

  static {
    ALL = Pattern.compile(".*");
    BASE_64 = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?");
    RFC_3339_DATE = Pattern.compile("^([0-9]+)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])");
    rfc3339_time = Pattern.compile("([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]|60)(\\.[0-9]+)?" +
                                   "(([Zz])|([+|\\-]([01][0-9]|2[0-3]):[0-5][0-9]))");
    RFC_3339_DATETIME = Pattern.compile(RFC_3339_DATE + "[Tt]" + rfc3339_time);
  }

  private RegexPatterns() {}
}
