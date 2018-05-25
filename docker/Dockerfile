FROM openjdk:8-jre-alpine

RUN apk --no-cache add stunnel

# Jolokia management and discovery, and application ports
# EXPOSE 8181 24884 8080 4443

#ENV LADOK3_DATABASE
#ENV LADOK3_USERNAME
#ENV LADOK3_PASSWORD

ENV LADOK3_CERT=/run/secrets/ladok3-user.crt
ENV LADOK3_CERT_KEY=/run/secrets/ladok3-user.key
ENV LADOK3_CA=/run/secrets/ca-chain.pem
ENV LADOK3_CRON=*/10+*+*+*+*+?

ADD integral-mecenat-integration-*.jar /opt/camel/application.jar
ADD opt /opt
RUN chmod +x /opt/camel/run.sh

WORKDIR /opt/data
WORKDIR /opt/camel
ENTRYPOINT ["/opt/camel/run.sh"]
CMD ["start"]
