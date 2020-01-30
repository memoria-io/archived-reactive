[![](https://travis-ci.org/IsmailMarmoush/jutils.svg?branch=master)](https://travis-ci.org/IsmailMarmoush/jutils?branch=master)
[![Codecove](https://codecov.io/github/ismailmarmoush/jutils/coverage.svg?precision=2)](https://codecov.io/gh/IsmailMarmoush/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.marmoush/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.marmoush/jutils/)

# jutils
jutils is a Java based Reactive utility library, an opinionated and pragmatic learning effort.
It came to light because I found much reused code after combining multiple libraries,  
one example is the combination of Project Reactor along with VAVR
it became clear that there was a need for functional utility methods like in 
[functional](src/main/java/com/marmoush/jutils/core/utils/functional) package

This library is on edge, and breaking changes are bound to happen occasionally,  
at least as long as the version's Major segment was still
[`0`](https://semver.org/#how-should-i-deal-with-revisions-in-the-0yz-initial-development-phase).

If you're using the library or like it, it would be great feedback if you star it. Thanks!.

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

## Related Articles
* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)

## Usage

```
<groupId>com.marmoush</groupId>
<artifactId>jutils</artifactId>
<version>0.3.4</version>
```

## Contribution
You can just do pull requests and I will check them asap.

## Todos
* Split to modules
* Argon hashing with Mono

## Release notes
* v0.3.5
  * Initial EventSourcing implementation
  * Fix infinite wait in kafka producer
* v0.3.4
  * Initial short implementation of kafka, nats and pulsar APIs with Reactor and Vavr
