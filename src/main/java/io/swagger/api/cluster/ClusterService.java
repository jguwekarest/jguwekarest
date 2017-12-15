package io.swagger.api.cluster;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.InputStream;

public abstract class ClusterService {
    public abstract Response clusterEMPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer numFolds, Integer numKMeansRuns, Integer maximumNumberOfClusters, Integer numClusters, Integer maxIterations, SecurityContext securityContext, String subjectid) throws Exception;
}
