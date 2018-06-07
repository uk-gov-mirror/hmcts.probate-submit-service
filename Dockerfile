FROM openjdk:8-alpine

RUN mkdir -p /usr/local/bin

COPY docker/entrypoint.sh /
COPY build/libs/submit-service*.jar /submit-service.jar

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy= curl --silent --fail http://localhost:8181/health

EXPOSE 8181

ENTRYPOINT [ "/entrypoint.sh" ]
