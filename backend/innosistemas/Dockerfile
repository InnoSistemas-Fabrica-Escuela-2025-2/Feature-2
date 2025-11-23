FROM amazoncorretto:21

WORKDIR /app
COPY target/innosistemas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]