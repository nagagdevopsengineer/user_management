FROM openjdk:11
RUN apt-get update 
RUN apt-get install -y maven
COPY  target/usermanagement-0.0.1-SNAPSHOT.jar user_management.jar
RUN bash -c 'touch /user_management.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","user_management.jar"]