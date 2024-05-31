#
# Build stage
#
FROM gradle:7.3.3-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Package stage
#
FROM openjdk:21
COPY --from=build /home/gradle/src/build/libs/geosurveymap-1.0.0.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Europe/Warsaw", "-Dspring.profiles.active=default,dev,secret", "-jar", "/usr/local/lib/app.jar"]
