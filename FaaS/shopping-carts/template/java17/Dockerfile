FROM eclipse-temurin:17-alpine as builder

RUN mkdir -p /home/app/libs

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"
WORKDIR /home/app

COPY *.gradle gradle.* gradlew ./
COPY gradle ./gradle

RUN ./gradlew --version

COPY . .

RUN ./gradlew build

FROM --platform=${TARGETPLATFORM:-linux/amd64} ghcr.io/openfaas/of-watchdog:0.9.10 as watchdog

FROM eclipse-temurin:17-alpine as ship
RUN apk --no-cache add \
   ca-certificates \
   zip
RUN addgroup --system app \
    && adduser --system --ingroup app app

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

WORKDIR /home/app
COPY --from=builder /home/app/function/build/distributions/function-1.0.zip ./function-1.0.zip
USER app
RUN unzip ./function-1.0.zip
WORKDIR /home/app/

ENV upstream_url="http://127.0.0.1:8082"
ENV mode="http"
ENV CLASSPATH="/home/app/function-1.0/function-1.0.jar:/home/app/function-1.0/libs/*"

ENV fprocess="java -XX:+UseContainerSupport com.openfaas.entrypoint.App"
EXPOSE 8080

HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

CMD ["fwatchdog"]
