# Stage 1: Build the application using Gradle
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# Copy Gradle wrapper and configuration files
COPY gradlew settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY app/build.gradle ./app/

# Make wrapper executable
RUN chmod +x ./gradlew

# Copy source code and test files
COPY app/src ./app/src

# Run tests and build distribution in a single step with capped memory
RUN ./gradlew :app:test :app:installDist --no-daemon -Dorg.gradle.jvmargs="-Xmx384m"

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built distribution from builder stage
COPY --from=builder /workspace/app/build/install/app ./

# Create data directory for AOF persistence volume
RUN mkdir -p /app/data

# Configurable environment variables
ENV PORT=6380
ENV JAVA_TOOL_OPTIONS="-Xmx256m"

# Mount point for persistent AOF logs
VOLUME ["/app/data"]

EXPOSE 6380

ENTRYPOINT ["bin/app"]
