package jgu.weka.api.factories;

import jgu.weka.api.ApiService;
import jgu.weka.api.impl.ApiImpl;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class ApiFactory {
    private final static ApiService service = new ApiImpl();

    public static ApiService getApiApi() {
        return service;
    }
}
