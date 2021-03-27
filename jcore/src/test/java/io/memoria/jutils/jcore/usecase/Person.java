package io.memoria.jutils.jcore.usecase;

import java.io.Serializable;

public record Person(String name, int age, Location l) implements Serializable {}
