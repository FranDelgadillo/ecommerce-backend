# Usamos OpenJDK 21
FROM openjdk:21-slim
VOLUME /tmp

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Exponemos el puerto en el que se ejecutará la aplicación
EXPOSE 8080

# Ejecutamos el JAR directamente desde el volumen montado
CMD ["java", "-jar", "/app/target/product-service-1.0.0.jar"]