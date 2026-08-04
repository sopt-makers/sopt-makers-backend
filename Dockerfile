FROM amazoncorretto:21
WORKDIR /app

ARG PROFILE=dev
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

COPY api/build/libs/*.jar app.jar
COPY gabia-tls.security gabia-tls.security

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.properties=/app/gabia-tls.security", "-jar", "app.jar"]
