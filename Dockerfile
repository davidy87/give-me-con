FROM openjdk:17
ARG JAR_FILE=/build/libs/give-me-con-0.0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]