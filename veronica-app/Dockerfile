FROM openjdk:8-jdk

RUN mkdir /tmp/keys
COPY target/classes/test-key.json /tmp/keys/service-account.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/tmp/keys/service-account.json

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/./urandom", "/app.jar"]