package io.memoria.reactive.core.text;

import java.io.Serializable;

public record Person(String name, int age, Location l) implements Serializable {}
