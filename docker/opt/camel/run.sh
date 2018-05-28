#!/bin/sh
set -e

cp -f "${LADOK3_CERT:?}" /opt/camel/etc/ladok3-user.crt && chmod 600 /opt/camel/etc/ladok3-user.crt
cp -f "${LADOK3_CERT_KEY:?}" /opt/camel/etc/ladok3-user.key && chmod 600 /opt/camel/etc/ladok3-user.key

stunnel /opt/camel/etc/stunnel.conf 2>&1 &

if [ "$*" = "start" ]; then
    exec java -jar application.jar --spring.config.location=file:/opt/data/,file:/run/secrets/
fi

exec $*
