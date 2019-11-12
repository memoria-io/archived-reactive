package com.marmoush.jutils.file;

import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileUtils {
  private FileUtils() {}

  public static InputStream asInputStream(String fileName) {
    return ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
  }

  public static Try<File> asFile(String fileName) {
    return Try.of(() -> new File(ClassLoader.getSystemClassLoader().getResource(fileName).toURI()));
  }

  public static String asString(String fileName, Function<String, String> lineFunc) {
    InputStream is = asInputStream(fileName);
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    return reader.lines().map(lineFunc).collect(Collectors.joining(System.lineSeparator()));
  }
}
