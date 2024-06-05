FROM eclipse-temurin:21-jdk-jammy
ENV JAR_NAME=app.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
#COPY build/libs/*.jar app.jar
COPY build/native/nativeCompile/spoty_api_v1 spoty_api
EXPOSE 8080
#ENTRYPOINT [ "java", "-jar", "app.jar" ]
ENTRYPOINT [ "./spoty_api" ]
