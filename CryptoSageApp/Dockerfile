# Etap 1: Build - użycie obrazu z Gradle do zbudowania aplikacji
FROM gradle:7.6-jdk21 AS builder

# Ustaw katalog roboczy dla Gradle
WORKDIR /app

# Skopiuj pliki Gradle i źródła do kontenera
COPY build.gradle settings.gradle ./
COPY src ./src

# Zbuduj aplikację za pomocą Gradle
RUN gradle build --no-daemon

# Etap 2: Run - użycie lżejszego obrazu JDK, aby uruchomić aplikację
FROM openjdk:21-jdk

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj wygenerowany plik JAR z etapu 1
COPY --from=builder /app/build/libs/CryptoSageApp-1.jar app.jar


# Uruchom aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]

