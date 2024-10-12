# Этап сборки
FROM maven:3.9.4-amazoncorretto-21 AS builder

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -T 1C

# Копируем исходный код
COPY src ./src

# Сборка приложения
RUN mvn clean package -DskipTests

# Этап выполнения
FROM openjdk:21-jdk-slim

# Копируем собранный .jar файл
COPY --from=builder target/*.jar app.jar

# Указываем, что приложение будет работать на порту 8080
EXPOSE 8080

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "/app.jar"]
