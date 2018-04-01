FROM anapsix/alpine-java:8_jdk

ENV DIR_NAME=/event-collector

WORKDIR $DIR_NAME

COPY maven/event-collector-*.tar.gz $DIR_NAME

RUN tar -xzvf event-collector-*.tar.gz --strip 1 -C . && rm event-collector-*.tar.gz

ENTRYPOINT ["bin/start.sh"]