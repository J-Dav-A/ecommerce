# ---- Etapa 1: compilar el JAR ----
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar -x test --no-daemon

# ---- Etapa 2: imagen liviana para ejecutar ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
# La nube define PORT; el perfil y las claves llegan por variables de entorno
ENTRYPOINT ["java", "-jar", "app.jar"]
