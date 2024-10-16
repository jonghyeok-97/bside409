FROM gradle:7.6-jdk17 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle build --no-daemon -x test

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*SNAPSHOT.jar bside-backend-admin.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "bside-backend-admin.jar"]