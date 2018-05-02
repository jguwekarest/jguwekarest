package io.swagger.api.cluster;

import io.swagger.api.factories.ClusterFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.HashMap;


//@Api(description = "The WEKA Clusterer API")
@Path("/cluster")
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


    @Context ServletContext servletContext;
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
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Resource Not Found")
    })
    public Response clusterEMPost(
        @Parameter(name = "file"
        //    schema = @Schema(type = "file", format = "binary"))
        )@FormDataParam("file") InputStream fileInputStream,
        @Parameter(name = "file"
        //    schema = @Schema(type = "file", format = "binary"), style = ParameterStyle.FORM
        ) @FormDataParam("file")  FormDataContentDisposition fileDetail,
        //@RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = "file",format = "file")))@FormDataParam("file2")  @QueryParam("file2") File file,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
        name = "datasetUri", extensions = @Extension(properties = {@ExtensionProperty(name = "orn-@test",  value = "testvalue")}))
        @FormDataParam("datasetURI")@QueryParam("datasetURI")  String datasetUri,
        @Parameter(description = "Number of folds to use when cross-validating to find the best number of clusters (default = 10)",
            schema = @Schema(defaultValue="10"))@FormDataParam("numFolds") @QueryParam("numFolds") Integer numFolds,
        @Parameter(description = "Number of runs of k-means to perform (default = 10)",
            schema = @Schema(defaultValue="10")) @FormDataParam("numKMeansRuns") @QueryParam("numKMeansRuns") Integer numKMeansRuns,
        @Parameter(description = "Maximum number of clusters to consider during cross-validation to select the best number of clusters (default = -1).",
            schema = @Schema(defaultValue = "-1"))@FormDataParam("maximumNumberOfClusters") @QueryParam("maximumNumberOfClusters") Integer maximumNumberOfClusters,
        @Parameter(description = "The number of clusters. -1 to select number of clusters automatically by cross validation (default = -1).",
            schema = @Schema(defaultValue="-1")) @FormDataParam("numClusters") @QueryParam("numClusters") Integer numClusters,
        @Parameter(description = "Maximum number of iterations (default = 100).",
            schema = @Schema(defaultValue = "100"))@FormDataParam("maxIterations") @QueryParam("maxIterations") Integer maxIterations,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("numFolds", numFolds);
        params.put("numKMeansRuns", numKMeansRuns);
        params.put("maximumNumberOfClusters", maximumNumberOfClusters);
        params.put("numClusters",numClusters);
        params.put("maxIterations",maxIterations);

        return delegate.clustererPost(fileInputStream, fileDetail, datasetUri, "EM", params, headers, ui, securityContext);

    }

    @POST
    @Path("/SimpleKMeans")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })

    @Operation(summary = "REST interface to the WEKA SimpleKMeans clusterer.",
        description = "REST interface to the WEKA SimpleKMeans clusterer.",
        tags={ "cluster" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/cluster/EM")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Cluster")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "SimpleKMeans", value = "http://weka.sourceforge.net/doc.dev/weka/clusterers/SimpleKMeans.html")
            })
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Resource Not Found")
        })
    public Response clusterSimpleKMeansPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @Parameter(description = "If using canopy clustering for initialization and/or speedup this is the maximum number of candidate canopies to retain in main memory during training of the canopy clusterer. T2 distance and data characteristics determine how many candidate canopies are formed before periodic and final pruning are performed. There may not be enough memory available if T2 is set too low.") @FormDataParam("canopyMaxNumCanopiesToHoldInMemory") Integer canopyMaxNumCanopiesToHoldInMemory,
        @Parameter(description = "If using canopy clustering for initialization and/or speedup this is the minimum T2-based density below which a canopy will be pruned during periodic pruning")@FormDataParam("canopyMinimumCanopyDensity") Double canopyMinimumCanopyDensity,
        @Parameter(description = "If using canopy clustering for initialization and/or speedup this is how often to prune low density canopies during training")@FormDataParam("canopyPeriodicPruningRate") Double canopyPeriodicPruningRate,
        @Parameter(description = "The T1 distance to use when using canopy clustering. Values < 0 are taken as a positive multiplier for the T2 distance")@FormDataParam("canopyT1") Double canopyT1,
        @Parameter(description = "The T2 distance to use when using canopy clustering. Values < 0 indicate that this should be set using a heuristic based on attribute standard deviation")@FormDataParam("canopyT2") Double canopyT2,
        @Parameter(description = "Display std deviations of numeric attributes and counts of nominal attributes.")@FormDataParam("displayStdDevs") Boolean displayStdDevs,
        @Parameter(description = "The distance function to use for instances comparison (default: weka.core.EuclideanDistance).")@FormDataParam("distanceFunction") String distanceFunction,
        @Parameter(description = "If set, clusterer capabilities are not checked before clusterer is built (Use with caution to reduce runtime).")@FormDataParam("doNotCheckCapabilities") Boolean doNotCheckCapabilities,
        @Parameter(description = "Replace missing values globally with mean/mode.")@FormDataParam("dontReplaceMissingValues") Boolean dontReplaceMissingValues,
        @Parameter(description = "Uses cut-off values for speeding up distance calculation, but suppresses also the calculation and output of the within cluster sum of squared errors/sum of distances.")@FormDataParam("fastDistanceCalc") Boolean fastDistanceCalc,
        @Parameter(description = "The initialization method to use. Random, k-means++, Canopy or farthest first.")@FormDataParam("initializationMethod") String initializationMethod,
        @Parameter(description = "Set maximum number of iterations.")@FormDataParam("maxIterations") Integer maxIterations,
        @Parameter(description = "Set number of clusters.")@FormDataParam("numClusters") Integer numClusters,
        @Parameter(description = "The number of execution slots (threads) to use. Set equal to the number of available cpu/cores.")@FormDataParam("numExecutionSlots") Integer numExecutionSlots,
        @Parameter(description = "Preserve order of instances.")@FormDataParam("preserveInstancesOrder") Boolean preserveInstancesOrder,
        @Parameter(description = "Use canopy clustering to reduce the number of distance calculations performed by k-means")@FormDataParam("reduceNumberOfDistanceCalcsViaCanopies") Boolean reduceNumberOfDistanceCalcsViaCanopies,
        @Parameter(description = "The random number seed to be used.")@FormDataParam("seed") Integer seed,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("canopyMaxNumCanopiesToHoldInMemory",canopyMaxNumCanopiesToHoldInMemory);
        params.put("canopyMinimumCanopyDensity", canopyMinimumCanopyDensity);
        params.put("canopyPeriodicPruningRate", canopyPeriodicPruningRate);
        params.put("canopyT1", canopyT1);
        params.put("canopyT2", canopyT2);
        params.put("displayStdDevs", displayStdDevs);
        params.put("distanceFunction", distanceFunction);
        params.put("doNotCheckCapabilities",doNotCheckCapabilities);
        params.put("dontReplaceMissingValues", dontReplaceMissingValues);
        params.put("fastDistanceCalc", fastDistanceCalc);
        params.put("initializationMethod", initializationMethod);
        params.put("maxIterations", maxIterations);
        params.put("numClusters", numClusters);
        params.put("numExecutionSlots", numExecutionSlots);
        params.put("preserveInstancesOrder", preserveInstancesOrder);
        params.put("reduceNumberOfDistanceCalcsViaCanopies", reduceNumberOfDistanceCalcsViaCanopies);
        params.put("seed",seed);
        return delegate.clustererPost(fileInputStream, fileDetail, datasetUri, "SimpleKMeans", params, headers, ui, securityContext);

    }



/*
    @POST
    @Path("/upload")
    @Operation(description = "TEST interface",
        summary = "Test.",
        tags={ "cluster" })
    @Tag(name="a1")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
                               @Parameter(name = "file",
                                   content = @Content(schema = @Schema(
                                   type = "object",
                                   format = "binary")
                                   )) @FormDataParam("file") @QueryParam("file") InputStream uploadedInputStream,
                               @Parameter(name = "file",
                                   description = "file bla", content = @Content(schema = @Schema(
                                   type = "object",
                                   format = "binary",
                                   description = "a file"))
                                   ) @FormDataParam("file") FormDataContentDisposition fileDetail
                                   //@FormDataParam("file1") FormDataBodyPart body


    ) {
        return Response.status(200).entity("File  " + fileDetail.getName() + " has been uploaded").build();
    }
*/
}
