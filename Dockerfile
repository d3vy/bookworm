# Этап сборки
FROM maven:3.8.6-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src
RUN mvn clean package -DskipTests


# Этап выполнения
# Используем официальный образ OpenJDK как базовый
FROM openjdk:21-jdk

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл jar вашего приложения в контейнер
COPY --from=build /app/target/Bot-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт, который будет использоваться приложением
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java","-jar","/app/app.jar"]
