FROM maven:3.8.1-openjdk-17-slim AS build
COPY /src /app/src
COPY /pom.xml /app

RUN mvn -f /app/pom.xml clean package

FROM openjdk:17-jdk-alpine
EXPOSE 8080
COPY --from=build /app/target/*.jar app_todolist.jar

ENTRYPOINT [ "java", "-jar", "/app_todolist.jar" ]
