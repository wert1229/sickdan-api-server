FROM openjdk:11

LABEL maintainer="wert1229@naver.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=build/libs/sickdan-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} sickdan.jar

ENTRYPOINT ["java", "-jar", "/sickdan.jar"]
