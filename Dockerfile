#FROM consul:0.9.2
FROM maven:3.5-jdk-8-alpine
FROM openjdk:11

RUN apt-get update 
RUN apt-get install -y maven
RUN apt-get update 
RUN apt-get install -y tree
#RUN which kill

RUN apt-get install -y wget unzip
RUN wget https://releases.hashicorp.com/consul/1.11.4/consul_1.11.4_linux_amd64.zip

RUN unzip consul_1.11.4_linux_amd64.zip
RUN mv consul /usr/local/bin/

RUN ls
RUN echo $PATH

RUN consul version



RUN mkdir /app

COPY src /app/src
#COPY target /app/target
COPY pom.xml /app
WORKDIR /app



RUN date
#RUN consul agent -data-dir=/tmp/consul | mvn package &
RUN consul agent leave | mvn package

#RUN consul agent -dev | mvn package #& #> /tmp/user_mng_docker.log 2>&1
RUN date
#RUN sleep 5m
#RUN kill $(ps aux | grep 'consul' | awk '{print $2}')
#CMD consul agent -dev | java -jar ./target/usermanagement-0.0.1-SNAPSHOT.jar

#RUN consul agent -data-dir /var/consul -config-dir /etc/consul.d/client
WORKDIR /app
#EXPOSE 8081
#EXPOSE 8500
#EXPOSE 8080
#EXPOSE 8300
EXPOSE 8082

#RUN ls ./target/*
#RUN cd /app/target
#CMD tree ./app
#CMD echo "---------------- $pwd"
#RUN sleep 5m &
#RUN e
CMD consul agent -dev | java -jar ./target/usermanagement-0.0.1-SNAPSHOT.jar


#CMD consul agent -dev -data-dir=/tmp/consul &

#RUN mvn package


#ENTRYPOINT ["java","-jar","./target/usermanagement-0.0.1-SNAPSHOT.jar"]



#CMD consul agent -dev && tail -f /dev/null


#, "start"]



