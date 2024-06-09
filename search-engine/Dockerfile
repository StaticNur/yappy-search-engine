FROM openjdk:22-jdk
LABEL "name"="search-engine"
ARG JAR_FILE=target/search-engine-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]