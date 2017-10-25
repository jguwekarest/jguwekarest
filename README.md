# JGU WEKA Rest Service

## Overview
This is an a swagger-enabled JAX-RS server. The API is in OpenAPI Specification Version 2.0 [OpenAPI-Specification 2.0](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md).
The service uses the [JAX-RS](https://jax-rs-spec.java.net/) framework.

To run the server, please execute the following:

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
docker run -d -p 27017:27017 mongo
```


### curl Examples

POST an arff file to the WEKA BayesNet algorithm using curl:

```
curl  -X POST -H "Content-Type: multipart/form-data" -F "file=@/yourpathtowekadata/weka-3-8-1/data/weather.nominal.arff;" -F "estimatorParams=0.5"  -F "searchAlgorithm=local.K2" -F useADTree=0 -F "estimator=SimpleEstimator" -F searchParams='-P 1 -S BAYES' http://0.0.0.0:8080/weka_rs/algorithm/BayesNet
```
