# Stage 1
FROM maven:3.8-jdk-11 AS  maven_build
COPY src /tmp/src/
COPY pom.xml /tmp/
COPY "./dev-azure-com-kth-integration-integration-settings.xml" /tmp/settings.xml
WORKDIR /tmp/

RUN mvn -s /tmp/settings.xml --quiet clean package

# Stage 2
FROM adoptopenjdk/openjdk13:alpine-slim

ENV LADOK3_CERT=/run/secrets/ladok3-user.crt
ENV LADOK3_CERT_KEY=/run/secrets/ladok3-user.key

RUN apk --no-cache add stunnel

COPY --from=maven_build /tmp/target/integral-mecenat-integration-*.jar /opt/camel/application.jar
ADD docker/opt /opt
RUN chmod +x /opt/camel/run.sh

WORKDIR /opt/camel
ENTRYPOINT ["/opt/camel/run.sh"]
CMD ["start"]
