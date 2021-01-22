package io.memoria.jutils.core.utils.text;

public class TextUtils {

  public static String safeSQLTableName(String value) {
    return value.replace(" ", "").replaceAll("[^A-Za-z0-9]", "");
  }

  private TextUtils() {}
}
