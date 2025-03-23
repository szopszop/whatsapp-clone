FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/user-service.jar user-service.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "user-service.jar"]
