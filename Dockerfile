# Use an official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use a minimal Java runtime for running the app
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/Online-Auction-Platform-0.0.1-SNAPSHOT.jar.original app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
