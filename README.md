# GitHub Proxy API

Aplikacja Proxy integrująca się z GitHub API v3, służąca do listowania repozytoriów użytkownika (z wyłączeniem forków).

## Technologia
- **Java 25** + **Spring Boot 4.0.1**
- **Spring RestClient** (komunikacja HTTP)
- **WireMock** (testy integracyjne)

## Uruchamianie
1. Skompiluj i uruchom aplikację:
   ```bash
   ./gradlew bootRun
   ```
2. Endpoint: `http://localhost:8080/api/repositories/{username}`
3. Wymagany nagłówek: `Accept: application/json`

## Testy
Uruchomienie testów integracyjnych (WireMock):
```bash
./gradlew test
```
