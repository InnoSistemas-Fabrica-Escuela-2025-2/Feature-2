# Usa una imagen de Maven con Java 21 para el build
FROM maven:3.9-eclipse-temurin-21 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos del proyecto
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Da permisos de ejecución al Maven Wrapper
RUN chmod +x ./mvnw

# Construye el proyecto
RUN ./mvnw clean package -DskipTests

# Usa una imagen ligera de Java 21 para ejecutar la app
FROM eclipse-temurin:21-jre-alpine

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
