FROM openjdk:11

WORKDIR /usr/app
ARG JAR_FILE
COPY ${JAR_FILE} /usr/app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]