FROM fedora:latest
ENV JAR_NAME=app.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build/native/nativeCompile/spoty_api_v1 spoty
EXPOSE 8080
ENTRYPOINT [ "./spoty" ]
