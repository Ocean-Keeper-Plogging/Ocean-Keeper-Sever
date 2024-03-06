FROM openjdk:11 AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

FROM openjdk:11
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./

ENTRYPOINT ["java", "-jar", "./OceanKeeper-0.0.1-SNAPSHOT.jar"]