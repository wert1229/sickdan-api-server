FROM java:8

LABEL maintainer="wert1229@naver.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/sickdan-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} sickdan.jar

ENTRYPOINT ["java", "-jar", "/sickdan"]
