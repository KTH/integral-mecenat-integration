#!/bin/sh
set -e

echo "$MECENAT_INTEGRATION_CRT" | awk  '{gsub("\\\\n","\n")};1' | base64 -d > /opt/camel/etc/ladok3-user.crt && chmod 600 /opt/camel/etc/ladok3-user.crt
echo "$MECENAT_INTEGRATION_KEY" | awk  '{gsub("\\\\n","\n")};1' | base64 -d > /opt/camel/etc/ladok3-user.key && chmod 600 /opt/camel/etc/ladok3-user.key

stunnel /opt/camel/etc/stunnel.conf 2>&1 &

if [ "$*" = "start" ]; then
    exec /busybox httpd -f -v -p 80 -c /home/static/httpd.conf & java -jar application.jar
fi

exec "$*"
