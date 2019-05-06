# Stage 1
FROM maven:3.6-jdk-8-alpine AS  maven_build
COPY src /tmp/src/
COPY pom.xml /tmp/
COPY "./dev-azure-com-kth-integration-integration-settings.xml" /tmp/settings.xml
WORKDIR /tmp/

RUN mvn -s /tmp/settings.xml --quiet clean package

# Stage 2
FROM openjdk:8-jre-alpine

ENV LADOK3_CERT=/run/secrets/ladok3-user.crt
ENV LADOK3_CERT_KEY=/run/secrets/ladok3-user.key


COPY --from=maven_build /tmp/target/integral-mecenat-integration-*.jar /opt/camel/application.jar
ADD opt /opt
RUN chmod +x /opt/camel/run.sh

WORKDIR /opt/camel
ENTRYPOINT ["/opt/camel/run.sh"]
CMD ["start"]
