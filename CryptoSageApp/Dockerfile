# Użyj oficjalnego obrazu Javy
FROM openjdk:21-jdk

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj pliki Gradle i źródła do obrazu
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Zainstaluj narzędzie xargs (znajduje się w pakiecie findutils)
RUN apt-get update && apt-get install -y findutils

# Ustaw uprawnienia do wykonywania skryptu Gradle
RUN chmod +x ./gradlew

# Zbuduj projekt za pomocą Gradle
RUN ./gradlew build --no-daemon

# Skopiuj wygenerowany plik JAR do uruchomienia aplikacji
RUN cp build/libs/CryptoSageApp-1.jar app.jar

# Uruchom aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]
