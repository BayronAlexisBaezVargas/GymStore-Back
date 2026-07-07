FROM eclipse-temurin:25-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/ms-usuario-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
