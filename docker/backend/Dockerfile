# =================================================================
# Stage 1: Build Application
# =================================================================
FROM maven:3.9.8-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code and build production artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# =================================================================
# Stage 2: Minimal Production Runtime
# =================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-privileged application user
RUN addgroup -S agrilink && adduser -S agrilink -G agrilink

# Copy packaged JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown -R agrilink:agrilink /app

USER agrilink

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
