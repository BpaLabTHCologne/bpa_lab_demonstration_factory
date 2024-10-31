# Stufe 1: Bauen der Anwendung
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stufe 2: Erstellen des Laufzeit-Containers
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/KafkaStream-1.0-SNAPSHOT.jar /app/kafka-stream.jar
ENTRYPOINT ["java", "-jar", "kafka-stream.jar"]

