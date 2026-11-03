# 1) Build the React frontend into Spring Boot's static folder
FROM node:20-alpine AS web
WORKDIR /app
COPY frontend/package*.json frontend/
RUN cd frontend && npm ci
COPY frontend frontend
RUN mkdir -p src/main/resources && cd frontend && npx vite build

# 2) Package the Spring Boot app (includes the built frontend)
FROM maven:3.9-eclipse-temurin-17 AS api
WORKDIR /app
COPY pom.xml .
COPY src src
COPY --from=web /app/src/main/resources/static src/main/resources/static
RUN mvn -q -B package -DskipTests

# 3) Run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=api /app/target/campusskill.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
