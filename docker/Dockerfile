FROM openjdk:8-jre-alpine

RUN apk --no-cache add stunnel

# Configurations with defaults.
ENV LADOK3_CERT=/run/secrets/ladok3-user.crt
ENV LADOK3_CERT_KEY=/run/secrets/ladok3-user.key

ADD integral-mecenat-integration-*.jar /opt/camel/application.jar
ADD opt /opt
RUN chmod +x /opt/camel/run.sh

WORKDIR /opt/data
WORKDIR /opt/camel
ENTRYPOINT ["/opt/camel/run.sh"]
CMD ["start"]
