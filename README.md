[![](https://travis-ci.org/IsmailMarmoush/jutils.svg?branch=master)](https://travis-ci.org/IsmailMarmoush/jutils?branch=master)
[![Codecove](https://codecov.io/github/ismailmarmoush/jutils/coverage.svg?precision=2)](https://codecov.io/gh/IsmailMarmoush/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.marmoush/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.marmoush/jutils/)

# jutils
jutils is a Java utility library, an opinionated and pragmatic learning effort.
It came to light because of reused code after combining multiple libraries.
For example the combination of Project Reactor along with VAVR 
it became clear that there was a need for functional utility methods like in 
[functional](src/main/java/com/marmoush/jutils/utils/functional) package 

This library is on edge, and breaking changes are bound to happen occasionally, at least as long as the version Major segment is
[`0`](https://semver.org/#how-should-i-deal-with-revisions-in-the-0yz-initial-development-phase).

If you're using the library or like it, it would be great feedback if you star it. Thanks!.


## Usage

```
<groupId>com.marmoush</groupId>
<artifactId>jutils</artifactId>
<version>0.3.4</version>
``` 

## Features
* Vavr + Reactor Functional utilities
* Reactor Netty adapter with error handling escalation
* Simple Reactive Message bus implementation for:
  * Apache Kafka
  * Nats
  * Apache Pulsar
* Yaml adapter with features like nesting, fluent api, resource and file loading
* Vavr GSON Json implementation
* Argon2 Hashing adapter
* In memory generic crud

## Contribution
You can just do pull requests and I will check them asap.

## Todos
* Argon hashing with Mono
* Initial EventSourcing implementation

## Release notes
* v0.3.4
  * Initial short implementation of kafka, nats and pulsar APIs with Reactor and Vavr
