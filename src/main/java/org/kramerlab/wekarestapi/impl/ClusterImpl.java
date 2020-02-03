package org.kramerlab.wekarestapi.impl;

import weka.clusterers.*;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToNominal;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.kramerlab.wekarestapi.WekaOptionHelper;
import org.kramerlab.wekarestapi.WekaUtils;
import org.kramerlab.wekarestapi.cluster.ClusterService;
import org.kramerlab.wekarestapi.data.DatasetService;

import java.io.InputStream;
import java.util.HashMap;

// FIXME Replace raw types with parameterized 
@SuppressWarnings("rawtypes")
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
