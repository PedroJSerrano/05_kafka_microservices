# Usa una imagen base de OpenJDK con la versión de Java que necesitas (Java 21)
# Alpine es una distribución ligera, ideal para reducir el tamaño de la imagen final.
FROM openjdk:21-jdk-slim-bullseye

# Instala curl para el healthcheck
RUN apt-get update && apt-get install -y curl --no-install-recommends && rm -rf /var/lib/apt/lists/*

# Define un argumento para el nombre del archivo JAR.
ARG JAR_FILE=target/stock-control-microservice-*.jar

# Opcional: Asegúrate de que el directorio de destino existe y es el WORKDIR
WORKDIR /app

# Copia el archivo JAR de tu aplicación al directorio /app dentro de la imagen.
COPY ${JAR_FILE} app.jar

# Expone el puerto en el que tu aplicación Spring Boot escuchará.
EXPOSE 8083

# Comando para ejecutar la aplicación Spring Boot cuando se inicie el contenedor.
# El perfil se activará mediante la variable de entorno SPRING_PROFILES_ACTIVE.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]