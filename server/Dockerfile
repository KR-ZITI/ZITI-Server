FROM openjdk:22-ea-1-jdk-oracle
VOLUME /tmp
EXPOSE 9090
ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]