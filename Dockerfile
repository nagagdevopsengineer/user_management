FROM openjdk:11
#ARG JAR_FILE=target/*.jar
RUN apt-get update 
RUN apt-get install -y maven
RUN mkdir /app
#COPY  usermanagement-0.0.1-SNAPSHOT.jar user_management.jar
COPY src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn package
EXPOSE 8080
ENTRYPOINT ["java","-jar","$(pwd)/target/usermanagement-0.0.1-SNAPSHOT.jar"]