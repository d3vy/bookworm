FROM openjdk:21-jdk-slim

# Установим рабочую директорию
WORKDIR /app

# Копируем JAR файл из этапа сборки
COPY target/Bot-0.0.1-SNAPSHOT.jar app.jar

# Открываем необходимый порт (убедитесь, что ваше приложение использует этот порт)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
