FROM eclipse-temurin:17-alpine as builder

RUN mkdir -p /home/app/libs

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"
WORKDIR /home/app

COPY *.gradle gradle.* gradlew ./
COPY gradle ./gradle

RUN ./gradlew --version

COPY . .

RUN ./gradlew function:distTar

FROM --platform=${TARGETPLATFORM:-linux/amd64} ghcr.io/openfaas/of-watchdog:0.9.11 as watchdog

FROM eclipse-temurin:17-alpine as ship
RUN apk --no-cache add \
   ca-certificates
RUN addgroup --system app \
    && adduser --system --ingroup app app

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

USER app
WORKDIR /home/app
COPY --from=builder /home/app/function/build/distributions/function-2.0.tar ./function-2.0.tar
RUN tar -xf ./function-2.0.tar


ENV upstream_url="http://127.0.0.1:8082"
ENV mode="http"

ENV fprocess="/home/app/function-2.0/bin/function"
EXPOSE 8080

HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

RUN chmod +x $fprocess

CMD ["fwatchdog"]
