package io.memoria.jutils.jcore.text;

import java.io.Serializable;

public record Person(String name, int age, Location l) implements Serializable {}
