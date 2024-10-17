FROM debian:bookworm-slim
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build/native/nativeCompile/spoty_api_v1 spoty
VOLUME /var/lib/opencore/static/uploads
EXPOSE 80
ENTRYPOINT [ "./spoty" ]
