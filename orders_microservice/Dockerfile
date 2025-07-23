# Usa una imagen base de OpenJDK con la versión de Java que necesitas (Java 21)
FROM openjdk:21-jdk-slim-bullseye

# Define un argumento para el nombre del archivo JAR.
ARG JAR_FILE=target/orders_microservice-*.jar

# Copia el archivo JAR de tu aplicación al directorio /app dentro de la imagen.
COPY ${JAR_FILE} app.jar

# Expone el puerto en el que tu aplicación Spring Boot escuchará.
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot cuando se inicie el contenedor.
# El perfil se activará mediante la variable de entorno SPRING_PROFILES_ACTIVE.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]