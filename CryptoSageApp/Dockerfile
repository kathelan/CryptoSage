# Użyj oficjalnego obrazu Javy
FROM openjdk:21-jdk

# Ustaw katalog roboczy
WORKDIR /app

# Kopiuj plik JAR do obrazu
COPY build/libs/CryptoSageApp-0.0.1-SNAPSHOT.jar app.jar

# Uruchom aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]
