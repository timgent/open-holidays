#!/usr/bin/env bash

BUILD_HOME_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
BUILD_IMAGE="quay.io/ukhomeofficedigital/scala-play:v0.1.4"

cd $BUILD_HOME_DIR
rm target/scala-2.11/*.jar
docker run -i --rm=true \
    -v ${PWD}:/code \
    -v ${PWD}/tmp/.ivy2:/root/.ivy2/ \
    -v ${PWD}/tmp/.sbt:/root/.sbt/ \
    "${BUILD_IMAGE}" activator assembly