# Dockerized Development Environment Setup

This document describes an example `Docker` images based development environment for a Java web project using a MongoDB database for data storage and a Tomcat application server for hosting the application WAR. Maven or a Jenkins server can be used as a build setup for the code deployment. Jenkins and Tomcat share a file storage for easy deployment via copying within the Jenkins instance.

## MongoDB Setup

Start a MongoDB container with local file storage, restart policy and a container name:

```
docker run -d --restart unless-stopped --name mongodb -v ~/mongodb:/data/db mongo
```

The MongoDB container can only be accessed by its local IP address.

## Tomcat Container setup with Maven and Docker build

Deploying the REST API on a Tomcat image using `docker build` requires a compiled war. This can be easily achieved with maven. Running the following command in the project root directory (which contains the pom.xml file) creates a war file in the `target` directory.

```
mvn clean package
```

Next, a Tomcat based image can be created with the packaged war file using the [Dockerfile](../Dockerfile) as follows:

```
docker build -t jguweka/jguweka:IMAGE_TAG -f Dockerfile .
```

where the IMAGE_TAG should refer to the desired tag of your created image. A detailed description of the Dockerfile is available [here](./DockerImageDeployment.md).

Finally, a test environment can be deployed using the following command:

```
docker run -it --restart unless-stopped --name weka_rest_app \
           --link mongodb:mongodb --rm -p 8080:8080 -p 8000:8000 \
           -e JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n" \
           jguweka/jguweka:IMAGE_TAG
```

The locally deployed service can then be accessed at the address`0.0.0.0:8080`.

An IDE can also be connected to the docker container for remote debugging using port 8000.

## Tomcat Container setup with Jenkins

If you are running a Jenkins server, you can use the following steps for creating and deploying the REST API.    

```
docker run -it --restart unless-stopped --name weka_rest_app --link mongodb:mongodb -p 8081:8081 -v ~/jenkins/workspace/tomcat:/usr/local/tomcat/webapps tomcat:8.5.47-jdk8
```

Add a proxy entry to  /usr/local/tomcat/conf/server.xml to proxy from host Apache2 or Nginx to the Tomcat docker IP:8081
```
<Connector port="8081" proxyPort="8081"/>
```

### Jenkins

Run a Jenkins Docker container on port 8089, with a filestorage on the hosts machine. Here the jenkins_home directory is stored at ~/jenkins in your homedirectory on the host machine.

```
docker run -e JENKINS_OPTS="--httpPort=8089" -p 8089:8089 -p 50000:50000 -v  ~/jenkins:/var/jenkins_home jenkins
```

### Jenkins project setup

Login to the jenkins web UI add a new project with the **New Item** button. 

* Add a **freestyle project** and give it a name.

* add your git repository to:
SOURCE CODE MANAGEMENT
https://{REPOSITORYURL}/jgu_weka_rest.git
Add authentication if needed.

* Add build step *Execute shell* to add mongodb IP address, port and authentication credentials.
Replace the mongodb IP address.
EXECUTE SHELL
```
echo "db.name=production\ndb.host={MONGODB_IP}\ndb.port=27017\n" > $WORKSPACE/src/main/resources/config/db.properties
```
If you use additional authentication (this not needed when running on the same network with the application, mongodb is not exposed to the internet) then (you have to add --auth too the docker run command above):
```
echo "db.name=production\ndb.host={MONGODB_IP}\ndb.port=27017\ndb.user={DBUSER}\ndb.password={PASSWORD}\n" > $WORKSPACE/src/main/resources/config/db.properties
```

* Add build step 
INVOKE TOP LEVEL MAVEN TARGETS
to compile the java code

```
maven clean package
```

* Copy the compiled war file to the tomcat containers file storage 
EXECUTE SHELL

```
cp /var/jenkins_home/workspace/weka_rest_service/target/weka_rs-{VERSION}.war /var/jenkins_home/workspace/tomcat/ROOT.war
```

## Tomcat Setup with Keycloak

For **Keycloak authentication** do this with [Dockerfile-keycloak](../Dockerfile-keycloak).   
Adjust settings for Keycloak as described in [Tomcat Keycloak Setup](./TomcatKeycloakSetup.md)

```
docker build -t jguweka/jguweka:keycloak -f Dockerfile-keycloak .
docker run -d  -p 0.0.0.0:8080:8080  --link mongodb:mongodb jguweka/jguweka:keycloak
```

## Apache2 Setup 

Enable mod proxy on the host computer.

```
a2enmod proxy proxy_html proxy_http
```

Add the proxying to the /etc/apache2/sites-enabled/ configuration file

```
ProxyPreserveHost On
ProxyPass / http://localhost:8080/
ProxyPassReverse / http://localhost:8080/
```