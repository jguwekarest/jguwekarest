package org.kramerlab.wekarestapi.factories;

import org.kramerlab.wekarestapi.cluster.ClusterService;
import org.kramerlab.wekarestapi.impl.ClusterImpl;

public class ClusterFactory  {
    private final static ClusterService service = new ClusterImpl();
    public static ClusterService getCluster() {
        return service;
    }
}
