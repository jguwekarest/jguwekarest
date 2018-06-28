# Dockerized Development Environment Setup

This describes an example dockerized development environment for a Java web project with a MongoDB database. A Jenkins server is used as a build setup for the code deployment. A Tomcat container hosts the application war file. Jenkins and Tomcat do share a file storage for easy deployment via copying within the Jenkins instance. 


## MongoDB Setup

Start a MongoDB container with a local file storage, restart policy and container name:

```
docker run -d --restart unless-stopped --name mongodb -v ~/mongodb:/data/db mongo
```
You can access MongoDB by local its IP not from the intenet. 


## Tomcat Container Setup

```
docker run -d --restart unless-stopped --name tomcat --link mongodb:mongodb -p 8081:8081 -v ~/jenkins/workspace/tomcat:/usr/local/tomcat/webapps tomcat:8.0-jre8
```

Add a proxy entry to  /usr/local/tomcat/conf/server.xml to proxy from host Apache2 or Nginx to the Tomcat docker IP:8081
```
<Connector port="8081" proxyPort="8081"/>
```


If you want to use OpenTox Authorization and Authentication you have to install a security certificate to the tomcat truststore. (This is used to connect to https://openam.in-silico.ch.)

Download the certificate from https://openam.in-silico.ch with openssl:
```
openssl s_client -showcerts -connect openam.in-silico.ch:443 </dev/null 2>/dev/null|openssl x509 -outform PEM >in-silicoch.crt
``` 
Import it into the Tomcat truststore
```
docker cp in-silicoch.crt ORN_tomcat:/usr/local/tomcat
docker exec -it ORN_tomcat keytool -keystore /etc/ssl/certs/java/cacerts -importcert -alias openam.in-silico.ch -file in-silicoch.crt
```

## Tomcat Setup with Dockerfile

Alternative you can build the tomcat image and container with needed certificate and java application with the [Dockerfile](../Dockerfile).

```
docker build -t jguweka/jguweka -f Dockerfile .
docker run -d  -p 0.0.0.0:8080:8080 --link mongodb:mongodb jguweka/jguweka
```

For **Keycloak authentication** do this with [Dockerfile-keycloak](../Dockerfile-keycloak).   
Adjust settings for Keycloak as described in [Tomcat Keycloak Setup](./TomcatKeycloakSetup.md)


```
docker build -t jguweka/jguweka:keycloak -f Dockerfile-keycloak .
docker run -d  -p 0.0.0.0:8080:8080  --link mongodb:mongodb jguweka/jguweka:keycloak
```

## Jenkins

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
```maven clean package```

* Copy the compiled war file to the tomcat containers file storage 
EXECUTE SHELL
```
cp /var/jenkins_home/workspace/weka_rest_service/target/weka_rs-{VERSION}.war /var/jenkins_home/workspace/tomcat/ROOT.war
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