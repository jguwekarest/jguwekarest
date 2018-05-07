FROM tomcat:8.0-jre8
MAINTAINER "M Rautenberg <rautenberg@uni-mainz.de>"

# remove preinstalled webapps 
RUN rm -fr /usr/local/tomcat/webapps/ROOT
RUN rm -fr /usr/local/tomcat/webapps/host-manager
RUN rm -fr /usr/local/tomcat/webapps/manager
RUN rm -fr /usr/local/tomcat/webapps/docs
RUN rm -fr /usr/local/tomcat/webapps/examples

COPY target/weka_rs-0.2.0.war /usr/local/tomcat/webapps/ROOT.war

# add openam certificat to tomcat's cert-store
RUN openssl s_client -showcerts -connect openam.in-silico.ch:443 </dev/null 2>/dev/null|openssl x509 -outform PEM > /usr/local/tomcat/in-silicoch.crt
RUN keytool -keystore /etc/ssl/certs/java/cacerts -keypass changeit -storepass changeit -noprompt -importcert -alias openam.in-silico.ch -file /usr/local/tomcat/in-silicoch.crt

EXPOSE 8080
