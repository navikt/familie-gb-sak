FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
COPY --chown=nonroot:nonroot ./target/gravferd-sak.jar /app/app.jar
WORKDIR /app

ENV APP_NAME=gravferd-sak
ENV TZ="Europe/Oslo"
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]