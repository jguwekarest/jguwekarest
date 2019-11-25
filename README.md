# JGU WEKA REST Service

## RESTful API web service to WEKA Machine Learning Algorithms

The JGU WEKA REST service provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA library.
This web service is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de/) at the [Johannes Gutenberg University Mainz](https://www.uni-mainz.de).

[OpenRiskNet](https://openrisknet.org/) is funded by the European Commission GA 731075.

WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.

[Documentation](https://jguwekarest.github.io/jguwekarest/), [Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) and [Code](https://github.com/jguwekarest/jguwekarest) available at GitHub.

## Quick start

The JGU WEKA REST service is based on the [Swagger-UI](https://swagger.io/tools/swagger-ui/) and [JAX-RS](https://jax-rs-spec.java.net/) frameworks. The API is in [OpenAPI Specification ver. 3.0.1](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md)

To run a local environment for exploring the web service, execute the following:

```
mvn clean package jetty:run
```

This will run the Swagger-UI based REST API web service on a local Jetty instance which can then be viewed at the following URI:

```
http://0.0.0.0:8081
```

To use the web service for modelling, etc., refer to the documents [Docker Image Deployment](./doc/DockerImageDeployment.md) and [Docker Development Environment](./doc/DockerizedDevEnvSetup.md).

### Usage with the *curl* command

`POST`ing a WEKA ARFF file to the web service and training a BayesNet based WEKA model using the `curl` command is done as follows:

```
curl -X POST -H "Content-Type: multipart/form-data" -H "Accept:text/x-arff" \
     -F "file=@/path/to/data/weather.nominal.arff;" -F "estimatorParams=0.5" \
     -F "searchAlgorithm=local.K2" -F "estimator=SimpleEstimator" \
     -F useADTree=0 -F searchParams='-P 1 -S BAYES' \
     http://0.0.0.0:8081/algorithm/BayesNet
```

## Documentation

 * Deploying a Dockerized environment **[local or server hosted development environment](./doc/DockerizedDevEnvSetup.md).** 
 * Deploying a Docker image: **[Build the Docker image with a Dockerfile](./doc/DockerImageDeployment.md)**.
 * Running tests: **[Run Tests](./doc/Testing.md)**.
 * OpenShift Deployment: **[Deployment in OpenShift](./openshift/README.md)**.
 * Examples of invoking model training/uploading dataset, etc using CLI/curl: **[Curl Examples](./doc/CommandlineCurlExamples.md)**.
 * Authentication: **[Keycloak Integration](./doc/TomcatKeycloakSetup.md)**.
 * Java docs on gh-pages: **[JavaDocs](https://jguwekarest.github.io/jguwekarest/javadoc/index.html)**.
 
