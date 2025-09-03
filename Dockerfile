# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
COPY --from=build /app/target/*.jar /wallet-settlement.jar
ENTRYPOINT ["java", "-jar", "/wallet-settlement.jar"]
