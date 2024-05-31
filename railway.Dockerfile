#
# Build stage
#
FROM gradle:8-jdk21 AS build
COPY src /home/app/src
COPY build.gradle.kts /home/app
COPY settings.gradle.kts /home/app
WORKDIR /home/app
RUN gradle build


# Package stage
#
FROM openjdk:21
COPY --from=build /home/gradle/src/build/libs/geosurveymap-1.0.0.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Europe/Warsaw", "-Dspring.profiles.active=default,dev,secret", "-jar", "/usr/local/lib/app.jar"]
