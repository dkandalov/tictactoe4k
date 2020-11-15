FROM gradle:6.7.0-jdk11 as cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/code/
WORKDIR /home/gradle/code
RUN gradle clean build -i --stacktrace

FROM gradle:6.7.0-jdk11 as build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /src
WORKDIR /src
RUN gradle --no-daemon -i -stacktrace shadowJar

FROM openjdk:11-jre
EXPOSE 8080
COPY --from=build /src/build/libs/tictactoe4k-all.jar /app/
WORKDIR /app
CMD ["java", "-cp", "tictactoe4k-all.jar", "MainKt"]