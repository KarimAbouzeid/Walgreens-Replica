FROM openjdk:21-jdk
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]