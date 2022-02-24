FROM openjdk:11
#ARG JAR_FILE=target/*.jar
RUN apt-get update 
RUN apt-get install -y maven
COPY  usermanagement-0.0.1-SNAPSHOT.jar user_management.jar
#COPY  target/usermanagement-0.0.1-SNAPSHOT.jar user_management.jar
RUN bash -c 'touch /user_management.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","user_management.jar"]