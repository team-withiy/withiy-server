# Stage 1: Build
FROM openjdk:17-jdk-slim AS build

ARG POSTGRES_HOST
ARG POSTGRES_PORT
ARG POSTGRES_USER
ARG POSTGRES_PASSWORD
ARG POSTGRES_DB
ARG JWT_SECRET_KEY
ARG HMAC_SECRET_KEY

ARG GOOGLE_CLIENT_ID
ARG GOOGLE_CLIENT_PASSWORD

ARG NAVER_CLIENT_ID
ARG NAVER_CLIENT_PASSWORD

ARG KAKAO_CLIENT_ID
ARG KAKAO_CLIENT_SECRET

# Mock environment values for test (related: issue #15)
ENV JWT_SECRET_KEY="CUL0gl15xbD4Y4DFRGCVBkLfXCodzgwOypSL82/HuD4="
ENV HMAC_SECRET_KEY="e1582d13fec7437be95eb68027ff2145ce553d56c93eb7a6dea29a68e1e337e7"

WORKDIR /server
COPY src /server/src/
COPY gradle /server/gradle/
COPY gradlew /server/
COPY build.gradle /server/
COPY settings.gradle /server/

RUN ./gradlew build -x test  #실행시간 단축 위해 테스트 빼고 진행 -> 테스트 실행하고 싶으면 -x test 지우세요

# Stage 2: Run
FROM openjdk:17-jdk-slim

LABEL maintainer="Zerohertz <ohg3417@gmail.com>"
LABEL description="withiy-server"
LABEL license="MIT"

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8


RUN apt-get update && apt-get install -y locales tzdata && \
    echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen en_US.UTF-8 && \
    update-locale LANG=en_US.UTF-8 && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

WORKDIR /server
COPY --from=build /server/build/libs/*.jar /server/app.jar

CMD ["java", "-jar", "/server/app.jar"]
