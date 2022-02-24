FROM openjdk:11
#ARG JAR_FILE=target/*.jar
RUN apt-get update 
RUN apt-get install -y maven
#COPY  usermanagement-0.0.1-SNAPSHOT.jar user_management.jar
COPY src . 
COPY pom.xml .
RUN mvn clean package -Dmaven.test.skip
EXPOSE 8080
ENTRYPOINT ["java","-jar","$(pwd)/target/usermanagement-0.0.1-SNAPSHOT.jar"]