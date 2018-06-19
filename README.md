# JGU WEKA Rest Service

RESTful API Webservice to WEKA Machine Learning Algorithms.
This webservice provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA Java Library.
This application is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de/) at the Johannes Gutenberg University Mainz.
OpenRiskNet is funded by the European Commission GA 731075. WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.

See [Documentation](https://jguwekarest.github.io/jguwekarest/), [Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) and [Code](https://github.com/jguwekarest/jguwekarest) at GitHub.

## Quickstart
This is an a swagger-enabled JAX-RS server. The API is in OpenAPI Specification Version 3.0.1 [OpenAPI-Specification 3.0.1](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md)
The service uses the [JAX-RS](https://jax-rs-spec.java.net/) framework.

To run a simple local environment, please execute the following:

```
mvn clean package jetty:run
```

You can then view the full Rest API on Swagger-UI here:

```
http://0.0.0.0:8081
```

To connect the server to a mongodb database you can use a standard mongo docker image pulled from docker hub:

```
docker pull mongo
docker run -d mongo
```

### *curl* Example

POST an arff file to the WEKA BayesNet algorithm using curl:
```
curl  -X POST -H "Content-Type: multipart/form-data" -H "Accept:text/x-arff" -F "file=@/yourpathtowekadata/weka-3-8-1/data/weather.nominal.arff;" -F "estimatorParams=0.5"  -F "searchAlgorithm=local.K2" -F useADTree=0 -F "estimator=SimpleEstimator" -F searchParams='-P 1 -S BAYES' http://0.0.0.0:8081/algorithm/BayesNet
```

## Documentation

 * Full example for a **[local or server hosted development environment](./doc/DockerizedDevEnvSetup.md).** 
 * Docker Deployment: **[Build the Docker image with a Dockerfile](./doc/DockerImageDeployment.md)**.
 * Running tests: **[Run Tests](./doc/Testing.md)**.
 * OpenShift Deployment: **[Deployment in OpenShift](./openshift/README.md)**.
 * Commandline Examples with Curl: **[Curl Examples](./doc/CommandlineCurlExamples.md)**.
 * Authentication: **[Keycloak Integration](./doc/TomcatKeyCloakSetup.md)**.
 * Java Docs on gh-pages: **[JavaDocs](https://jguwekarest.github.io/jguwekarest/javadoc/index.html)**.
 