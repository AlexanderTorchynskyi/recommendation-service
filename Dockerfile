FROM openjdk:11.0.11-jre-slim

WORKDIR /app

COPY target/*.jar /app/recommendation.jar

ENTRYPOINT ["java","-jar","recommendation.jar"]