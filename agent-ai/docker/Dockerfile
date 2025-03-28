# Etap 1: Build - użycie obrazu z Gradle do zbudowania aplikacji
FROM gradle:jdk21 AS builder

# Ustaw katalog roboczy dla Gradle
WORKDIR /app

# Skopiuj pliki Gradle i źródła do kontenera
COPY ../src /app/src

# Zbuduj aplikację za pomocą Gradle
RUN gradle build --no-daemon

# Etap 2: Run - użycie lżejszego obrazu JDK, aby uruchomić aplikację
FROM openjdk:21-jdk

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj wygenerowany plik JAR z etapu 1
COPY --from=builder /app/build/libs/agent-ai-1.0.0.jar app.jar


# Uruchom aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]

