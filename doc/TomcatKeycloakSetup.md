# Tomcat Keycloak Setup
This document describes Keycloak authentication setup for the JGU WEKA REST service running in docker tomcat environment.

See main documentation  ***[Deploy using Keycloak](https://github.com/OpenRiskNet/example-java-servlet/tree/master/openshift/keycloak)*** at OpenRiskNet GitHub for Keycloak integration.



### Define a new client in the SSO realm <span style="font-size:0.6em">(Manual Approach)</span>

* Login to the Redhat Single Sing-On interface and switch into the openrisknet realm. 
* Left hand menu click on Clients. 
* Click on the Create button  
 ![Click on the Create button](./pics/createclient1.png)
* Import the client template [jguweka.json](../openshift/keycloak/jguweka.json)
 ![Import the client template](./pics/importclient.png)
* Press save and adjust the client settings, URLs, ...
* Save this again
* Switch to the Credentials tab and copy the 36 character long HexadecimalNumber credential.
  ![Copy Creadential](./pics/copycredential.png)
* Set this credential as a value in [jguweka.json](../openshift/keycloak/keycloak.json)   
  example:  
  ```
    "credentials" : {
      "secret": "123456789-abcd-abcd-abcd-1234567890ab"
    },
  ``` 
 
tbc ...