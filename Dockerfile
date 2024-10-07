FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]