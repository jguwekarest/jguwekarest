# How to Build the Docker Image

This document describes how to setup and start a docker image and its container for the JGU WEKA Rest service. 


## Setup the Dockerfile
The current docker image is build with a simple Dockerfile that 
uses a tomcat base-image from [hub.docker.com/_/tomcat](https://hub.docker.com/_/tomcat/), the official Apache Tomcat repository.    
The Dockerfile removes all pre-installed webapps from tomcat and copies the JGU WEKA Rest service war file to */usr/local/tomcat/webapps/ROOT.war*
as the main application.   
Then it installs the SSL certificates from the openam.in-silico.ch Authentication & Authorization server to the tomcat keystore.
Finally port 8080 is exposed. 

    
```
FROM tomcat:8.0-jre8
MAINTAINER "MyName <xyz@uni-mainz.de>"

# remove preinstalled webapps 
RUN rm -fr /usr/local/tomcat/webapps/ROOT
RUN rm -fr /usr/local/tomcat/webapps/host-manager
RUN rm -fr /usr/local/tomcat/webapps/manager
RUN rm -fr /usr/local/tomcat/webapps/docs
RUN rm -fr /usr/local/tomcat/webapps/examples

COPY target/weka_rs-<VERSION_NUMBER>.war /usr/local/tomcat/webapps/ROOT.war

# add openam certificat to tomcat's cert-store
RUN openssl s_client -showcerts -connect openam.in-silico.ch:443 </dev/null 2>/dev/null|openssl x509 -outform PEM > /usr/local/tomcat/in-silicoch.crt
RUN keytool -keystore /etc/ssl/certs/java/cacerts -keypass changeit -storepass changeit -noprompt -importcert -alias openam.in-silico.ch -file /usr/local/tomcat/in-silicoch.crt

EXPOSE 8080
```
Customise the [Dockerfile](../Dockerfile) as needed (e.g.: adjust the war file name and version).  

## Build the Docker Image

* Download the source code from Github   
`git clone git@github.com:jguwekarest/jguwekarest.git`
* Change into code directory   
`cd jguwekarest`
* Checkout branch (optional)   
  ***master*** for OpenAPI 3.0.1, ***OAS2*** for OpenApi 2.0 version    
  `git checkout master`
* Compile the war (Web Application Archive) file with maven   
  `mvn clean package`
* Build the docker image (replace dockerhubuser with your docker hub account user)   
  `docker build -t dockerhubuser/jguweka:OAS3 .`
* Check images    
  `docker images`

## Run the Docker Container

* Run the image as a local container 
`docker run -p 8080:8080 --link mongodb:mongodb dockerhubuser/jguweka:OAS3`
* If you run the container locally don't forget to start also a mongodb container as a data base with:   
`docker pull mongo; docker run --name mongodb -d mongo`
* Load the Swagger-UI representation in a web-browser   
e.g.: `firefox http://0.0.0.0:8080`

## Documentation

* [jguwekarest documentation on github](https://jguwekarest.github.io/jguwekarest/)

**See also:**

* [Dockerfile Reference](https://docs.docker.com/engine/reference/builder/)
* [Tomcat Official Repository](https://hub.docker.com/r/_/tomcat/)
* [Docker Official Image packaging for Apache Tomcat ](https://github.com/docker-library/tomcat/) 