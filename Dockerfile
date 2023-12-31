FROM maven:3.5-jdk-8-alpine
FROM openjdk:11

RUN apt-get update 
RUN apt-get install -y maven

RUN apt-get install -y wget unzip
RUN wget https://releases.hashicorp.com/consul/1.11.4/consul_1.11.4_linux_amd64.zip

RUN unzip consul_1.11.4_linux_amd64.zip
RUN mv consul /usr/local/bin/

RUN consul version
RUN mvn -version

RUN mkdir /app
COPY src /app/src
COPY pom.xml /app
WORKDIR /app

RUN consul agent leave | mvn package

WORKDIR /app

EXPOSE 8082

CMD consul agent -dev | java -jar ./target/usermanagement-0.0.1-SNAPSHOT.jar