# Etapa de build
FROM gradle:8.14.3-jdk17 AS builder

WORKDIR /app

COPY . .

RUN gradle build -x test

# Etapa de ejecución
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]