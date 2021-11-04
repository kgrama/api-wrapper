FROM gradle:7.2.0-jdk11-alpine AS BUILD_IMAGE
ENV APP_HOME=/opt/app/
ADD build.gradle $APP_HOME
WORKDIR $APP_HOME
RUN gradle build || return 0 
ADD . .
RUN gradle build
RUN ls build/libs/

FROM adoptopenjdk/openjdk11:alpine
ENV ARTIFACT_NAME=api-wrapper-demo-0.0.1-SNAPSHOT.jar
RUN mkdir /app
COPY --from=BUILD_IMAGE /opt/app/build/libs/$ARTIFACT_NAME /app/spring-boot-application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]
