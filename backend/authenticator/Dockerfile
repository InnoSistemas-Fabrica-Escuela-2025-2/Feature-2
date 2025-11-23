FROM amazoncorretto:21

WORKDIR /app
COPY target/authenticator-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]