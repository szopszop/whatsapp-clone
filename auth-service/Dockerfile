FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/auth-service.jar auth-service.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "auth-service.jar"]
