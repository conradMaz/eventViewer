FROM anapsix/alpine-java:8_jdk

ENV DIR_NAME=/postman

WORKDIR $DIR_NAME

COPY maven/postman-*.tar.gz $DIR_NAME

RUN tar -xzvf postman-*.tar.gz --strip 1 -C . && rm postman-*.tar.gz
RUN ls -l /bin/sh

ENTRYPOINT ["bin/start.sh"]