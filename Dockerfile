FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/customer-service-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]