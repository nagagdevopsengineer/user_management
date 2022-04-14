FROM consul
FROM openjdk:11

RUN apt-get update 
RUN apt-get install -y maven

RUN apt-get install -y wget unzip
RUN wget https://releases.hashicorp.com/consul/1.11.4/consul_1.11.4_linux_amd64.zip

RUN unzip consul_1.11.4_linux_amd64.zip
RUN mv consul /usr/local/bin/

RUN ls
RUN echo $PATH

RUN consul version


RUN mkdir /app

COPY src /app/src
COPY pom.xml /app
WORKDIR /app

EXPOSE 8500
RUN mvn package

CMD consul agent -dev


#ENTRYPOINT ["java","-jar","./target/usermanagement-0.0.1-SNAPSHOT.jar"]

#CMD consul agent -dev | mvn package

#CMD consul agent -dev -data-dir=/tmp/consul &

#RUN mvn package


#ENTRYPOINT ["java","-jar","./target/usermanagement-0.0.1-SNAPSHOT.jar"]



#CMD consul agent -dev && tail -f /dev/null


#, "start"]



