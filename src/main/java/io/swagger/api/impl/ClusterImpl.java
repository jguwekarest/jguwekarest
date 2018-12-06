package io.swagger.api.impl;

import io.swagger.api.WekaOptionHelper;
import io.swagger.api.WekaUtils;
import io.swagger.api.cluster.ClusterService;
import io.swagger.api.data.DatasetService;
import weka.clusterers.*;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToNominal;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.HashMap;

public class ClusterImpl extends ClusterService {
    @Override
    @Produces("text/plain")
    public Response clustererPost(InputStream fileInputStream, String datasetUri, String clustererName, HashMap params,
                                  HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws Exception {

        String subjectid = headers.getRequestHeaders().getFirst("subjectid");
        String txtStr = DatasetService.getArff(fileInputStream, datasetUri, subjectid);
        String baseuri = ui.getBaseUri().toString();
        String accept = headers.getRequestHeaders().getFirst("accept");
        Instances instances = WekaUtils.instancesFromString(txtStr, false);

        String[] options;
        try {
            options = WekaOptionHelper.getClassifierOptions(clustererName, params);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        AbstractClusterer clusterer;
        clusterer = getClusterer(clustererName);
        clusterer.setOptions(options);

        StringToNominal s2n = new StringToNominal();
        s2n.setAttributeRange("first-last");

        s2n.setInputFormat(instances);
        Instances newData = new Instances(StringToNominal.useFilter(instances, s2n));

        clusterer.buildClusterer(newData);

        // evaluate clusterer
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);
        eval.evaluateClusterer(newData);

        return Response.ok(eval.clusterResultsToString()).build();
    }

    AbstractClusterer getClusterer(String clustererName){
        AbstractClusterer clusterer = null;
        try {
            switch (clustererName) {
                case "EM":
                    clusterer = new EM();
                    break;
                case "HierarchicalClusterer":
                    clusterer = new HierarchicalClusterer();
                    break;
                case "SimpleKMeans":
                    clusterer = new SimpleKMeans();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return clusterer;
    }

}
