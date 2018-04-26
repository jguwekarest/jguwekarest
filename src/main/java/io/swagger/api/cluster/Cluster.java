package io.swagger.api.cluster;

import io.swagger.annotations.*;
import io.swagger.api.factories.ClusterFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.HashMap;

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


    @Context ServletContext servletContext;
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
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
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

    @ApiOperation(value = "REST interface to the WEKA SimpleKMeans clusterer.",
        notes = "REST interface to the WEKA SimpleKMeans clusterer.",
        tags={ "cluster" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/cluster/EM")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Cluster")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "SimpleKMeans", value = "http://weka.sourceforge.net/doc.dev/weka/clusterers/SimpleKMeans.html")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found")
    })
    public Response clusterSimpleKMeansPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "If using canopy clustering for initialization and/or speedup this is the maximum number of candidate canopies to retain in main memory during training of the canopy clusterer. T2 distance and data characteristics determine how many candidate canopies are formed before periodic and final pruning are performed. There may not be enough memory available if T2 is set too low.") @FormDataParam("canopyMaxNumCanopiesToHoldInMemory") Integer canopyMaxNumCanopiesToHoldInMemory,
        @ApiParam(value = "If using canopy clustering for initialization and/or speedup this is the minimum T2-based density below which a canopy will be pruned during periodic pruning")@FormDataParam("canopyMinimumCanopyDensity") Double canopyMinimumCanopyDensity,
        @ApiParam(value = "If using canopy clustering for initialization and/or speedup this is how often to prune low density canopies during training")@FormDataParam("canopyPeriodicPruningRate") Double canopyPeriodicPruningRate,
        @ApiParam(value = "The T1 distance to use when using canopy clustering. Values < 0 are taken as a positive multiplier for the T2 distance")@FormDataParam("canopyT1") Double canopyT1,
        @ApiParam(value = "The T2 distance to use when using canopy clustering. Values < 0 indicate that this should be set using a heuristic based on attribute standard deviation")@FormDataParam("canopyT2") Double canopyT2,
        @ApiParam(value = "Display std deviations of numeric attributes and counts of nominal attributes.")@FormDataParam("displayStdDevs") Boolean displayStdDevs,
        @ApiParam(value = "The distance function to use for instances comparison (default: weka.core.EuclideanDistance).")@FormDataParam("distanceFunction") String distanceFunction,
        @ApiParam(value = "If set, clusterer capabilities are not checked before clusterer is built (Use with caution to reduce runtime).")@FormDataParam("doNotCheckCapabilities") Boolean doNotCheckCapabilities,
        @ApiParam(value = "Replace missing values globally with mean/mode.")@FormDataParam("dontReplaceMissingValues") Boolean dontReplaceMissingValues,
        @ApiParam(value = "Uses cut-off values for speeding up distance calculation, but suppresses also the calculation and output of the within cluster sum of squared errors/sum of distances.")@FormDataParam("fastDistanceCalc") Boolean fastDistanceCalc,
        @ApiParam(value = "The initialization method to use. Random, k-means++, Canopy or farthest first.")@FormDataParam("initializationMethod") String initializationMethod,
        @ApiParam(value = "Set maximum number of iterations.")@FormDataParam("maxIterations") Integer maxIterations,
        @ApiParam(value = "Set number of clusters.")@FormDataParam("numClusters") Integer numClusters,
        @ApiParam(value = "The number of execution slots (threads) to use. Set equal to the number of available cpu/cores.")@FormDataParam("numExecutionSlots") Integer numExecutionSlots,
        @ApiParam(value = "Preserve order of instances.")@FormDataParam("preserveInstancesOrder") Boolean preserveInstancesOrder,
        @ApiParam(value = "Use canopy clustering to reduce the number of distance calculations performed by k-means")@FormDataParam("reduceNumberOfDistanceCalcsViaCanopies") Boolean reduceNumberOfDistanceCalcsViaCanopies,
        @ApiParam(value = "The random number seed to be used.")@FormDataParam("seed") Integer seed,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
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



}
