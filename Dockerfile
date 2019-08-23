ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.0
LABEL maintainer="https://github.com/hmcts/probate-submit-service"
COPY build/libs/probate-orchestrator-service.jar /opt/app/

COPY build/libs/submit-service.jar /opt/app
HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8181/health || exit 1
EXPOSE 8181
CMD [ "submit-service.jar" ]