package io.swagger.api.factories;

import io.swagger.api.cluster.ClusterService;
import io.swagger.api.impl.ClusterImpl;

public class ClusterFactory  {
    private final static ClusterService service = new ClusterImpl();
    public static ClusterService getCluster() {
        return service;
    }
}
