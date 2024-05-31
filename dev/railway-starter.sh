#!/bin/sh

echo "Building the Spring Boot application..."
./gradlew clean build

echo "Building the Docker image..."
docker build -t spring-boot-app ../.

echo "Running the Docker container..."
docker run -p 8080:8080 spring-boot-app -d

echo "Application is now running on http://localhost:8080"
