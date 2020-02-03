FROM tomcat:8.5.47-jdk8
MAINTAINER "Atif Raza <atifraza@uni-mainz.de>"

EXPOSE 8080

# remove preinstalled webapps and manager
RUN rm -fr /usr/local/tomcat/webapps/*

# Create a non-priviledged user to run Tomcat
RUN useradd -u 501 -m -g root tomcat && chown -R tomcat:root /usr/local/tomcat

# add the application war file
ADD --chown=tomcat:root target/weka_rs.war /usr/local/tomcat/webapps/ROOT.war

# run as that user
USER 501
