FROM openjdk:11
ARG JAR_FILE=target/*.jar
RUN apt-get update 
RUN apt-get install -y maven
#RUN bash -c 'touch /*.jar'
EXPOSE 8080
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]