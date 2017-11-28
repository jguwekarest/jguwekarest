package io.swagger.api.factories;

import io.swagger.api.cluster.ClusterService;
import io.swagger.api.impl.ClusterImpl;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-11-23T11:08:00.572Z")
public class ClusterFactory  {
    private final static ClusterService service = new ClusterImpl();
    public static ClusterService getCluster() {
        return service;
    }
}
