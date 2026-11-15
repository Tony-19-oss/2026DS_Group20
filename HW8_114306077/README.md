# 尋知 — Tavily Search

A Spring Boot search page that queries the Tavily Search API and displays result titles, links, and summaries.

## Requirements

- Java 17 or newer
- Maven 3.6 or newer
- A Tavily API key

## Run

Set the API key and enable the provider before starting the application. In PowerShell:

```powershell
$env:TAVILY_ENABLED = "true"
$env:TAVILY_API_KEY = "your-tavily-api-key"
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The key is read from the environment and is not stored in the repository.
