package io.memoria.jutils.jcore.file;

import java.io.Serializable;

public record Person(String name, int age, Location l) implements Serializable {}
