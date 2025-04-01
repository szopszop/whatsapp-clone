FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/auth-server-*.jar auth-server.jar

ENV SPRING_PROFILES_ACTIVE=docker

CMD ["java", "-jar", "auth-server.jar"]
