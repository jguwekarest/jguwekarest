FROM tomcat:8.0-jre8
MAINTAINER "Atif Raza <atifraza@uni-mainz.de>"

# remove preinstalled webapps 
RUN rm -fr /usr/local/tomcat/webapps/ROOT
RUN rm -fr /usr/local/tomcat/webapps/host-manager
RUN rm -fr /usr/local/tomcat/webapps/manager
RUN rm -fr /usr/local/tomcat/webapps/docs
RUN rm -fr /usr/local/tomcat/webapps/examples

# copy and unzip the application war file
ADD target/weka_rs.war /usr/local/tomcat/webapps/ROOT.war
RUN unzip -d /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war && rm -f /usr/local/tomcat/webapps/ROOT.war

# Create a non-priviledged user to run Tomcat
RUN useradd -u 501 -m -g root tomcat && chown -R tomcat:root /usr/local/tomcat
# Set file permissions for that user.
RUN chown -R tomcat:root /usr/local/tomcat
# run as that user
USER 501

EXPOSE 8080
