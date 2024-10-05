# Stage 1: Build
FROM maven:3.9-amazoncorretto-21 AS build

# Установим рабочую директорию
WORKDIR /build

# Копируем только pom.xml для кеширования зависимостей
COPY pom.xml .

# Скачиваем зависимости
RUN mvn dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:21-jdk-slim

# Установим рабочую директорию
WORKDIR /app

# Копируем JAR файл из этапа сборки
COPY --from=build /build/target/*.jar app.jar

# Открываем необходимый порт (убедитесь, что ваше приложение использует этот порт)
EXPOSE 8081

# Устанавливаем переменные окружения (можно также использовать docker-compose для этого)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bookworm_telegram_bot_database
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=glhf5561783742
ENV TELEGRAM_BOT_TOKEN=7768787297:AAFk5RXeryDQzOohK1wxNMHieOBXrsIZmj0

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
