FROM amazoncorretto:21
WORKDIR /app

ARG PROFILE=dev
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

COPY api/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
