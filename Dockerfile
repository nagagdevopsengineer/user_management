FROM openjdk:11
RUN apt-get update 
ARG JAR_FILE=target/*.jar 
RUN apt-get install -y maven
#ADD pom.xml /code/pom.xml 
#RUN ["mvn", "dependency:resolve"]   
#RUN  ["mvn","clean","install"]
COPY  target/user_management.jar user_management.jar
RUN bash -c 'touch /user_management.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","user_management.jar"]