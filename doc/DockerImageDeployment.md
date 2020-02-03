# How to Build the Docker Image

This document describes how to setup a docker image and then start a container based on that image for the JGU WEKA Rest service.

## Setup the Dockerfile
The docker image is built with a simple Dockerfile that 
uses a Tomcat base-image from [hub.docker.com/_/tomcat](https://hub.docker.com/_/tomcat/), the official Apache Tomcat repository.

The Dockerfile exposes port 8080, removes all pre-installed webapps from the Tomcat server, creates a new user and grants this user the ownership of the */usr/local/tomcat/* directory.    
Finally, the war built by maven is copied to */usr/local/tomcat/webapps/ROOT.war* and the proper ownership is granted.    

    
```
FROM tomcat:8.5.47-jdk8
MAINTAINER "Maintainer Name <xyz@uni-mainz.de>"

EXPOSE 8080

RUN rm -fr /usr/local/tomcat/webapps/*    # remove preinstalled webapps and manager

RUN useradd -u 501 -m -g root tomcat && chown -R tomcat:root /usr/local/tomcat    # Create a non-priviledged user to run Tomcat

ADD --chown=tomcat:root target/weka_rs.war /usr/local/tomcat/webapps/ROOT.war    # add the application war file

USER 501    # run as new user
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

* Run a mongodb container as a data base with:   
`docker pull mongo; docker run --name mongodb -d mongo`
* Run the Weka REST API image as a local container 
`docker run -p 8080:8080 --link mongodb:mongodb dockerhubuser/jguweka:OAS3`
* Load the Swagger-UI representation in a web-browser   
e.g.: `firefox http://0.0.0.0:8080`

## Documentation

* [JGU WEKA REST API Documentation](https://jguwekarest.github.io/jguwekarest/)

**See also:**

* [Dockerfile Reference](https://docs.docker.com/engine/reference/builder/)
* [Tomcat Official Dockerhub Repository](https://hub.docker.com/r/_/tomcat/)
* [Docker Official Image packaging for Apache Tomcat ](https://github.com/docker-library/tomcat/) 