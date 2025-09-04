# Use OpenJDK 17 slim image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/UserProfileManager-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8081 to the outside world
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8081"]
