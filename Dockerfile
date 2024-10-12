# Этап сборки
FROM maven:3.9.4-amazoncorretto-21 AS build

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src

RUN mvn clean package -DskipTests

# Этап выполнения
FROM openjdk:21-jdk-slim

COPY --from=build /target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]






