# Recruitment Task - GitHub Repository Viewer

A Spring Boot application that fetches GitHub repositories and branches for a given user and exposes them via REST API.

---

## Features

- Fetch repositories of a GitHub user.
- Fetch branches for each repository.
- Handles errors gracefully (e.g., user not found).
- Fully tested with **WireMock** and **MockMvc**.

---

## Tech Stack

- Java 25
- Spring Boot
- Spring Web (REST)
- JUnit 5
- WireMock (for integration tests)
- MockMvc (for controller testing)
- Gradle-Kotlin (build tool)

---

## Getting Started

### Prerequisites

- Java 25
- Gradle
- Internet connection for GitHub API access (unless using WireMock)

### Running the Application

```bash
./mvnw spring-boot:run
