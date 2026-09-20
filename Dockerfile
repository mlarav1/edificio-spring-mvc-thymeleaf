FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/edificio-spring-mvc-1.0.0.jar app.jar
# Limite de memoria para el plan gratuito (512 MB)
ENV JAVA_TOOL_OPTIONS="-Xmx300m"
EXPOSE 8080
# Render define $PORT; application.properties lo lee (server.port=${PORT:8080})
CMD ["java", "-jar", "app.jar"]
