package io.swagger.api.cluster;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.HashMap;

public abstract class ClusterService {
    public abstract Response clustererPost(InputStream fileInputStream, String datasetUri, String clusterer, HashMap params, HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws Exception;
}
