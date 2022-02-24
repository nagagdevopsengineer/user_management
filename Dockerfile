FROM maven:3.5.2-jdk-8-alpine
#RUN apt-get update 
ARG JAR_FILE=target/*.jar 
#RUN apt-get install -y maven
ADD pom.xml /code/pom.xml 
#RUN ["mvn", "dependency:resolve"]   
RUN mvn package
COPY  target/*.jar *.jar
RUN bash -c 'touch /*.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","user_management.jar"]