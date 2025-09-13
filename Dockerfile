# Step 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the JAR
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render will inject PORT env var, so we expose 8080 by default
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
