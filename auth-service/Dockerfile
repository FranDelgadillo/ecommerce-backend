# Usamos OpenJDK 21
FROM openjdk:21-slim
VOLUME /tmp

# Definimos el nombre del archivo JAR específico generado por Maven
ARG JAR_FILE=target/auth-service-1.0.0.jar

# Copiamos el archivo JAR al contenedor
COPY ${JAR_FILE} app.jar

# Exponemos el puerto en el que se ejecutará la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
