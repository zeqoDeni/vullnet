FROM maven:3.9.5-eclipse-temurin-17-alpine AS BUILD

COPY ./ ./

RUN mvn clean package

FROM eclipse-temurin:17_35-jdk-alpine

RUN mkdir -p /app

WORKDIR /app

COPY --from=BUILD target/service.jar service.jar

CMD ["java", "-jar", "service.jar"]
