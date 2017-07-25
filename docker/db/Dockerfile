FROM openjdk:8-jre

RUN mkdir /runtime
COPY *.jar /runtime
RUN ln -s /runtime/*.jar /runtime/pan-discovery-db.jar

ENV JAVA_OPTS       -Xmx512m
ENV db_url          jdbc:
ENV db_user         user
ENV db_password     pass


VOLUME /report
WORKDIR /report

CMD java \
        ${JAVA_OPTS} \
        -Dspring.datasource.url=${db_url} \
        -Dspring.datasource.username=${db_user} \
        -Dspring.datasource.password=${db_password} \
        -jar /runtime/pan-discovery-db.jar
