FROM openjdk:8-jre

RUN mkdir /runtime
COPY *.jar /runtime
RUN ln -s /runtime/*.jar /runtime/pan-discovery-fs.jar

ENV JAVA_OPTS       -Xmx512m

VOLUME /scanfolder
VOLUME /report

WORKDIR /report

CMD java \
        ${JAVA_OPTS} \
        -jar /runtime/pan-discovery-fs.jar \
        --verbose /scanfolder
