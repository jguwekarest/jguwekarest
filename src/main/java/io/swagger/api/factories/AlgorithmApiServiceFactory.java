package io.swagger.api.factories;

import io.swagger.api.AlgorithmApiService;
import io.swagger.api.impl.AlgorithmApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class AlgorithmApiServiceFactory {
    private final static AlgorithmApiService service = new AlgorithmApiServiceImpl();

    public static AlgorithmApiService getAlgorithmApi() {
        return service;
    }
}
