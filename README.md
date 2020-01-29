[![](https://travis-ci.org/IsmailMarmoush/jutils.svg?branch=master)](https://travis-ci.org/IsmailMarmoush/jutils?branch=master)
[![Codecove](https://codecov.io/github/ismailmarmoush/jutils/coverage.svg?precision=2)](https://codecov.io/gh/IsmailMarmoush/jutils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.marmoush/jutils/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.marmoush/jutils/)

# jutils
jutils is a Java Utils library, that came to light because of reused code after combining multiple libraries.
For example the combination of Project Reactor along with VAVR 
it became clear that there was a need for functional utility methods like in 
[functional](src/main/java/com/marmoush/jutils/utils/functional) package 
    
You might look at jutils as a new reactive Guava like library but of course not as mature as Guava yet.
You can also look at it as a dumb of code that I see reused, and can be extracted and generalized, therefore it is opinionated indeed.
But you can definitely consider this library as an ongoing pragmatic learning effort,
with my humble understanding to couple of theories and methods.
 
Therefore this library is on edge, and breaking changes are bound to happen occasionally.

Beware that currently unfortunately I'm not following the semantic versioning literally,
but one rule I'm trying to follow is having the Major segment as `0` to
indicate [initial](https://semver.org/#how-should-i-deal-with-revisions-in-the-0yz-initial-development-phase) breaking releases.

Rest assured that as soon as it gets more stability and users (if you're using it, please star it) there will hopefully be less breaking changes.


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
* Eventsourcing

## Release notes
* v0.3.4
  * Initial short implementation of kafka, nats and pulsar APIs with Reactor and Vavr
