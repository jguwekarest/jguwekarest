# JGU WEKA REST Service

## RESTful API web service to WEKA Machine Learning Algorithms.

This web service provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA library.
This web service is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de/) at the Johannes Gutenberg University Mainz.
OpenRiskNet is funded by the European Commission GA 731075. WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.

[Documentation](https://jguwekarest.github.io/jguwekarest/), [Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) and [Code](https://github.com/jguwekarest/jguwekarest) available at GitHub.

## Quick start
This web service is based on the [Swagger-UI](https://swagger.io/tools/swagger-ui/) and the [JAX-RS](https://jax-rs-spec.java.net/) framework. The API is in OpenAPI Specification Version 3.0.1 [OpenAPI-Specification 3.0.1](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md)

To run a local environment for exploring the web service, execute the following:

```
mvn clean package jetty:run
```

This will run the Swagger-UI based REST API web service on a local Jetty instance which can then be viewed at the following URI:

```
http://0.0.0.0:8081
```

To connect the web service to a MongoDB instance, you can use a standard mongo docker image pulled from docker hub:

```
docker pull mongo
docker run -d mongo
```

### *curl* Example

POST an ARFF file to the web service and train a WEKA BayesNet algorithm based model using curl:

```
curl  -X POST -H "Content-Type: multipart/form-data" -H "Accept:text/x-arff" -F "file=@/path/to/data/weather.nominal.arff;" -F "estimatorParams=0.5"  -F "searchAlgorithm=local.K2" -F useADTree=0 -F "estimator=SimpleEstimator" -F searchParams='-P 1 -S BAYES' http://0.0.0.0:8081/algorithm/BayesNet
```

## Documentation

 * Deploying a Dockerized environment **[local or server hosted development environment](./doc/DockerizedDevEnvSetup.md).** 
 * Deploying a Docker image: **[Build the Docker image with a Dockerfile](./doc/DockerImageDeployment.md)**.
 * Running tests: **[Run Tests](./doc/Testing.md)**.
 * OpenShift Deployment: **[Deployment in OpenShift](./openshift/README.md)**.
 * Examples of invoking model training/uploading dataset, etc using CLI/curl: **[Curl Examples](./doc/CommandlineCurlExamples.md)**.
 * Authentication: **[Keycloak Integration](./doc/TomcatKeycloakSetup.md)**.
 * Java docs on gh-pages: **[JavaDocs](https://jguwekarest.github.io/jguwekarest/javadoc/index.html)**.
 
