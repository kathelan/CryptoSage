# CryptoSage

Crypto Trading Bot
Projekt: Bot Handlowy Kryptowalut
Opis projektu
Crypto Trading Bot to aplikacja napisana w Javie z użyciem Spring Boot, która umożliwia automatyczny handel kryptowalutami na giełdzie Zonda (dawniej BitBay). Aplikacja wykorzystuje zaawansowane strategie handlowe oraz mechanizmy zarządzania ryzykiem, aby maksymalizować zyski i minimalizować straty.

Wymagania biznesowe
Automatyzacja handlu: Bot powinien automatycznie podejmować decyzje o zakupie i sprzedaży kryptowalut na podstawie zdefiniowanej strategii.
Zarządzanie ryzykiem: Implementacja mechanizmów zarządzania ryzykiem, takich jak stop-loss i take-profit, aby chronić inwestycje użytkownika.
Skalowalność: Możliwość dodania i konfiguracji różnych strategii handlowych oraz obsługi wielu par walutowych.
Testowanie: Aplikacja powinna umożliwiać testowanie strategii handlowych na danych historycznych bez ryzykowania prawdziwych środków.
Wymagania funkcjonalne
Integracja z API Zondy: Aplikacja musi łączyć się z API giełdy Zonda w celu pobierania danych rynkowych i realizacji zleceń.
Podstawowe operacje handlowe:
Pobieranie aktualnych cen kryptowalut.
Składanie zleceń kupna i sprzedaży.
Strategie handlowe:
Implementacja strategii MACD (Moving Average Convergence Divergence).
Możliwość konfiguracji strategii za pomocą parametrów.
Zarządzanie ryzykiem:
Implementacja zleceń stop-loss i take-profit.
Testowanie:
Możliwość uruchomienia aplikacji w trybie testowym, używając danych historycznych do testowania strategii.
Technologie
Java 11
Spring Boot
Maven/Gradle
API Zondy
Lombok
Knowm XChange


1. Tworzenie klasy do obsługi Zonda API
 Utwórz klasę ZondaService do obsługi połączeń z API Zondy.
 Implementuj metody do pobierania danych rynkowych i składania zleceń.
2. Tworzenie kontrolera REST
 Utwórz kontroler REST TradingBotController.
 Dodaj endpointy do pobierania cen i składania zleceń.
3. Implementacja strategii handlowej
 Utwórz pakiet strategy.
 Dodaj klasę MACDStrategy implementującą strategię MACD.
 Dodaj logikę handlową do ZondaService.
4. Zarządzanie ryzykiem
 Dodaj mechanizmy stop-loss i take-profit do ZondaService.
5. Testowanie aplikacji
 Utwórz tryb testowy używając danych historycznych.
 Przetestuj aplikację uruchamiając różne scenariusze handlowe.
