FROM openjdk:21
EXPOSE 8080
ADD build/libs/geosurveymap-1.0.0.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=Europe/Warsaw", "-Dspring.profiles.active=default,dev", "-jar", "app.jar"]
