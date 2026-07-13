FROM gradle:9.6.1-jdk17-corretto-al2023 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
COPY --chown=gradle:gradle gradle ./gradle
RUN ./gradlew --version
COPY --chown=gradle:gradle . .
RUN ./gradlew buildFatJar --no-daemon

FROM amazoncorretto:17-al2023 AS runtime
EXPOSE 8080
RUN mkdir -p /app /data
COPY --from=build /home/gradle/src/build/libs/ecomm-all.jar /app/ecomm.jar
ENTRYPOINT ["java","-jar","/app/ecomm.jar"]
