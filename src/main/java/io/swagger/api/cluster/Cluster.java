package io.swagger.api.cluster;

import io.swagger.annotations.*;
import io.swagger.api.factories.ClusterFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.InputStream;

@Path("/cluster")
@Api(description = "The WEKA Clusterer API")

public class Cluster {

    private final ClusterService delegate;

    public Cluster(@Context ServletConfig servletContext) {
        ClusterService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Cluster.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (ClusterService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = ClusterFactory.getCluster();
        }
        this.delegate = delegate;
    }


    @Context
    ServletContext servletContext;
    @POST
    @Path("/EM")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })

    @ApiOperation(value = "REST interface to the WEKA EM clusterer.",
        notes = "REST interface to the WEKA EM (expectation maximisation) clusterer.",
        tags={ "cluster" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/cluster/EM")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Cluster")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "EM", value = "https://en.wikipedia.org/wiki/Expectation%E2%80%93maximization_algorithm")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found")
    })
    public Response clusterEMPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "Number of folds to use when cross-validating to find the best number of clusters (default = 10)", defaultValue="10")@FormDataParam("numFolds") Integer numFolds,
        @ApiParam(value = "Number of runs of k-means to perform (default = 10)", defaultValue="10") @FormDataParam("numKMeansRuns") Integer numKMeansRuns,
        @ApiParam(value = "Maximum number of clusters to consider during cross-validation to select the best number of clusters (default = -1).", defaultValue = "-1")@FormDataParam("maximumNumberOfClusters") Integer maximumNumberOfClusters,
        @ApiParam(value = "The number of clusters. -1 to select number of clusters automatically by cross validation (default = -1).", defaultValue="-1") @FormDataParam("numClusters") Integer numClusters,
        @ApiParam(value = "Maximum number of iterations (default = 100).", defaultValue = "100")@FormDataParam("maxIterations") Integer maxIterations,
        @Context SecurityContext securityContext, @HeaderParam("subjectid") String subjectid)
        throws Exception {

            return delegate.clusterEMPost(fileInputStream, fileDetail, datasetUri, numFolds, numKMeansRuns, maximumNumberOfClusters,
                numClusters, maxIterations, securityContext, subjectid);

    }

}
