package io.swagger.api.cluster;

import io.swagger.api.factories.ClusterFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.InputStream;

@Path("/cluster")
//@Api(description = "The WEKA Clusterer API")

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
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ "text/x-arff" })
    @Operation(summary = "REST interface to the WEKA EM clusterer.",
        description =  "REST interface to the WEKA EM (expectation maximisation) clusterer.",
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
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found")
    })
    public Response clusterEMPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,

        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            name = "datasetUri", extensions = @Extension(properties = {@ExtensionProperty(name = "orn-@test",  value = "testvalue")}))@QueryParam("datasetURI")  String datasetUri,
        @Parameter(description = "Number of folds to use when cross-validating to find the best number of clusters (default = 10)",
            schema = @Schema(defaultValue="10"))@QueryParam("numFolds") Integer numFolds,
        @Parameter(description = "Number of runs of k-means to perform (default = 10)",
            schema = @Schema(defaultValue="10")) @QueryParam("numKMeansRuns") Integer numKMeansRuns,
        @Parameter(description = "Maximum number of clusters to consider during cross-validation to select the best number of clusters (default = -1).",
            schema = @Schema(defaultValue = "-1"))@QueryParam("maximumNumberOfClusters") Integer maximumNumberOfClusters,
        @Parameter(description = "The number of clusters. -1 to select number of clusters automatically by cross validation (default = -1).",
            schema = @Schema(defaultValue="-1")) @QueryParam("numClusters") Integer numClusters,
        @Parameter(description = "Maximum number of iterations (default = 100).",
            schema = @Schema(defaultValue = "100"))@QueryParam("maxIterations") Integer maxIterations,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {

            return delegate.clusterEMPost(fileInputStream, fileDetail, datasetUri, numFolds, numKMeansRuns, maximumNumberOfClusters,
                numClusters, maxIterations, securityContext, subjectid);

    }

    @POST
    @Path("/upload")
    @Operation(description = "TEST interface",
        summary = "Test.",
        tags={ "cluster" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("fileIdRenamed") final String fileId,
                               @Parameter(name = "file",
                                   description = "file bla",
                                   schema = @Schema(
                                       type = "file",
                                       format = "file",
                                       description = "a file")
                               ) @FormParam("file") File blubb) {
        return Response.status(200).entity("File  " + fileId + " has been uploaded").build();
    }

}
