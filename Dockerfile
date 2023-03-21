FROM maven:3.8-openjdk-17-slim AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /usr/src/app/target/challenge-tenpo-0.0.1-SNAPSHOT.jar /usr/app/challenge-tenpo-0.0.1-SNAPSHOT.jar
EXPOSE 8080:8080
ENTRYPOINT ["java","-jar","/usr/app/challenge-tenpo-0.0.1-SNAPSHOT.jar"]