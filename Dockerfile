FROM quay.io/ukhomeofficedigital/openjdk8-jre:v0.2.0
MAINTAINER tim.gent@digital.homeoffice.gov.uk

EXPOSE 8080

RUN mkdir /app

WORKDIR /app
COPY target/scala-2.11/*.jar /app/
COPY run.sh /app/
RUN groupadd -r scala && \
    useradd -r -g scala scala -d /app && \
    chown -R scala:scala /app

USER scala
ENTRYPOINT /usr/bin/env bash /app/run.sh
