# Этап сборки
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src
RUN mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/Bot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
