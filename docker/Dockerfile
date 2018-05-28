FROM openjdk:8-jre-alpine

RUN apk --no-cache add stunnel

# Configurations without defaults.
#ENV LADOK3_DATABASE
#ENV LADOK3_USERNAME
#ENV LADOK3_PASSWORD

# Configurations with defaults.
ENV LADOK3_CERT=/run/secrets/ladok3-user.crt
ENV LADOK3_CERT_KEY=/run/secrets/ladok3-user.key
ENV LADOK3_CRON=*/10+*+*+*+*+?
ENV LADOK3_OUTPUT_DIR=/opt/data/mecenat

ADD integral-mecenat-integration-*.jar /opt/camel/application.jar
ADD opt /opt
RUN chmod +x /opt/camel/run.sh

WORKDIR /opt/data
WORKDIR /opt/camel
ENTRYPOINT ["/opt/camel/run.sh"]
CMD ["start"]
