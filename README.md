# GCP IP Ranges API

A Spring Boot application in Kotlin that provides filtered access to Google Cloud Platform IP ranges.

## Running the Application local

### Prerequisites

- Java 21
- Gradle 8.x

### Option 1: Local Build and Run

```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

### Option 2: Run with Docker

```bash
# 1. Build Docker Image
docker build -t gcp-ip-ranges-api .
```

```bash
# 2. Start Container
docker run -d --name gcp-ip-api -p 8080:8080 gcp-ip-ranges-api
```

### Curl-Tests for running Container or local Start
```bash
# Asia all IP versions
curl -s "http://localhost:8080/api/ip-ranges?region=AS" | head -5

# Europe IPv4
curl -s "http://localhost:8080/api/ip-ranges?region=EU&ipVersion=IPv4" | head -5

# US IPv6
curl -s "http://localhost:8080/api/ip-ranges?region=US&ipVersion=IPv6" | head -5

# North america (incl. US)
curl -s "http://localhost:8080/api/ip-ranges?region=NA&ipVersion=IPv4" | head -5

# Invalid region
curl "http://localhost:8080/api/ip-ranges?region=INVALID"
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```