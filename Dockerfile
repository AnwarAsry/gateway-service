FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/gateway-service-1.0.0.jar app.jar
EXPOSE 9003
ENTRYPOINT ["java", "-jar", "app.jar"]