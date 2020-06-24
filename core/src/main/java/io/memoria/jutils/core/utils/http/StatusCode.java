package io.memoria.jutils.core.utils.http;

public enum StatusCode {
  $200(200, "OK"),
  $201(201, "Created"),
  $202(202, "Accepted"),
  $203(203, "Non-authoritative"),
  $204(204, "No Content"),
  $205(205, "Reset Content"),
  $206(206, "Partial Content"),
  $300(300, "Multiple Choices"),
  $301(301, "Moved Permanently"),
  $302(302, "Moved Temporarily"),
  $303(303, "Might be found in"),
  $400(400, "Bad Request"),
  $401(401, "Unauthorized"),
  $403(403, "Forbidden"),
  $404(404, "Not Found"),
  $405(405, "Method Not Allowed"),
  $410(410, "Gone"),
  $500(500, "Internal Server Error"),
  $501(501, "Not Implemented"),
  $503(503, "Service Unavailable");

  public final int code;
  public final String description;

  private StatusCode(int code, String longDescription) {
    this.code = code;
    this.description = longDescription;
  }

  @Override
  public String toString() {
    return code + ": " + description;
  }
}
