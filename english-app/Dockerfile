FROM openjdk:17
EXPOSE 8080
WORKDIR /app
COPY target/english-app-0.0.1-SNAPSHOT.jar english-app.jar
ENTRYPOINT ["java", "-jar", "english-app.jar"]
