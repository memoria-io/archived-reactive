#!/bin/bash

compile(){
  docker run -it \
          --volume ${PWD}:/jutils \
          --workdir /jutils \
          --network jutils_default \
          ismailmarmoush/jbom:latest /bin/bash -c "mvn install"
}

$@
