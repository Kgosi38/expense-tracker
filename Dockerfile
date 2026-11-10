
# Stage 1: Build the application using Maven and Java 26
FROM maven:3.9-eclipse-temurin-26 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Download project dependencies
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests -B


# Stage 2: Run the application using Java 26
FROM eclipse-temurin:26-jre

WORKDIR /app

# Copy the compiled application
COPY --from=build /app/target/*.jar app.jar

# Enable the read-only demo profile
ENV SPRING_PROFILES_ACTIVE=demo

# Default application port
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
