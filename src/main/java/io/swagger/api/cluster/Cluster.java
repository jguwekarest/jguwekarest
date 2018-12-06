package io.swagger.api.cluster;

import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.api.factories.ClusterFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
        })
    @GroupedApiResponsesOk
    public Response clusterEMPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
        name = "datasetUri", extensions = @Extension(properties = {@ExtensionProperty(name = "orn-@test",  value = "testvalue")})))
        @FormDataParam("datasetUri") String datasetUri,
        @Parameter(schema = @Schema(description = "Number of folds to use when cross-validating to find the best number of clusters (default = 10)",
            defaultValue="10"))@FormDataParam("numFolds") Integer numFolds,
        @Parameter(schema = @Schema(description = "Number of runs of k-means to perform (default = 10)",
            defaultValue="10")) @FormDataParam("numKMeansRuns") Integer numKMeansRuns,
        @Parameter(schema = @Schema(description = "Maximum number of clusters to consider during cross-validation to select the best number of clusters (default = -1).",
            defaultValue = "-1"))@FormDataParam("maximumNumberOfClusters") Integer maximumNumberOfClusters,
        @Parameter(schema = @Schema(description = "The number of clusters. -1 to select number of clusters automatically by cross validation (default = -1).",
            defaultValue="-1")) @FormDataParam("numClusters") Integer numClusters,
        @Parameter(schema = @Schema(description = "Maximum number of iterations (default = 100).",
            defaultValue = "100"))@FormDataParam("maxIterations") Integer maxIterations,
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

            return delegate.clustererPost(fileInputStream, datasetUri, "EM", params, headers, ui, securityContext);

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
        })
    @GroupedApiResponsesOk
    public Response clusterSimpleKMeansPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = ""))@FormDataParam("datasetUri") String datasetUri,
        @Parameter(schema = @Schema(description = "If using canopy clustering for initialization and/or speedup this is the maximum number of candidate canopies to retain in main memory during training of the canopy clusterer. T2 distance and data characteristics determine how many candidate canopies are formed before periodic and final pruning are performed. " +
            "There may not be enough memory available if T2 is set too low.")) @FormDataParam("canopyMaxNumCanopiesToHoldInMemory") Integer canopyMaxNumCanopiesToHoldInMemory,
        @Parameter(schema = @Schema(description = "If using canopy clustering for initialization and/or speedup this is the minimum T2-based density below which a canopy will be pruned " +
            "during periodic pruning")) @FormDataParam("canopyMinimumCanopyDensity") Double canopyMinimumCanopyDensity,
        @Parameter(schema = @Schema(description = "If using canopy clustering for initialization and/or speedup this is how often to prune low density canopies during training"))
            @FormDataParam("canopyPeriodicPruningRate") Double canopyPeriodicPruningRate,
        @Parameter(schema = @Schema(description = "The T1 distance to use when using canopy clustering. Values < 0 are taken as a positive multiplier for the T2 distance"))
            @FormDataParam("canopyT1") Double canopyT1,
        @Parameter(schema = @Schema(description = "The T2 distance to use when using canopy clustering. Values < 0 indicate that this should be set using a heuristic based " +
            "on attribute standard deviation"))
            @FormDataParam("canopyT2") Double canopyT2,
        @Parameter(schema = @Schema(description = "Display std deviations of numeric attributes and counts of nominal attributes."))
            @FormDataParam("displayStdDevs") Boolean displayStdDevs,
        @Parameter(schema = @Schema(description = "The distance function to use for instances comparison (default: EuclideanDistance) " +
            "(e.g.: 'EuclideanDistance -R first-last', 'ManhattanDistance -R 1,2,3,4')."))
            @FormDataParam("distanceFunction") String distanceFunction,
        @Parameter(schema = @Schema(description = "Replace missing values globally with mean/mode."))
            @FormDataParam("dontReplaceMissingValues") Boolean dontReplaceMissingValues,
        @Parameter(schema = @Schema(description = "Uses cut-off values for speeding up distance calculation, but suppresses also the calculation and output of the within " +
            "cluster sum of squared errors/sum of distances."))
            @FormDataParam("fastDistanceCalc") Boolean fastDistanceCalc,
        @Parameter(schema = @Schema(description = "The initialization method to use. Random(0), k-means++(1), Canopy(2) or farthest first(3).",
            defaultValue = "0",allowableValues = {"0","1","2","3"}))
            @FormDataParam("initializationMethod") Integer initializationMethod,
        @Parameter(schema = @Schema(description = "Set maximum number of iterations."))
            @FormDataParam("maxIterations") Integer maxIterations,
        @Parameter(schema = @Schema(description = "Set number of clusters."))
            @FormDataParam("numClusters") Integer numClusters,
        @Parameter(schema = @Schema(description = "The number of execution slots (threads) to use. Set equal to the number of available cpu/cores."))
            @FormDataParam("numExecutionSlots") Integer numExecutionSlots,
        @Parameter(schema = @Schema(description = "Preserve order of instances."))
            @FormDataParam("preserveInstancesOrder") Boolean preserveInstancesOrder,
        @Parameter(schema = @Schema(description = "Use canopy clustering to reduce the number of distance calculations performed by k-means"))
            @FormDataParam("reduceNumberOfDistanceCalcsViaCanopies") Boolean reduceNumberOfDistanceCalcsViaCanopies,
        @Parameter(schema = @Schema(description = "The random number seed to be used.")) @FormDataParam("seed") Integer seed,
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
            params.put("dontReplaceMissingValues", dontReplaceMissingValues);
            params.put("fastDistanceCalc", fastDistanceCalc);
            params.put("initializationMethod", initializationMethod);
            params.put("maxIterations", maxIterations);
            params.put("numClusters", numClusters);
            params.put("numExecutionSlots", numExecutionSlots);
            params.put("preserveInstancesOrder", preserveInstancesOrder);
            params.put("reduceNumberOfDistanceCalcsViaCanopies", reduceNumberOfDistanceCalcsViaCanopies);
            params.put("seed",seed);
            return delegate.clustererPost(fileInputStream, datasetUri, "SimpleKMeans", params, headers, ui, securityContext);
    }


    @POST
    @Path("/Hierarchical")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })

    @Operation(summary = "REST interface to the WEKA Hierarchical clusterer.",
        description = "REST interface to the WEKA Hierarchical clusterer.",
        tags={ "cluster" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/cluster/Hierarchical")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Cluster")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "SimpleKMeans", value = "http://weka.sourceforge.net/doc.dev/weka/clusterers/HierarchicalClusterer.html")
            })
        })
    @GroupedApiResponsesOk
    public Response clusterHierarchicalPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = ""))
            @FormDataParam("datasetUri") String datasetUri,
        @Parameter(schema = @Schema(description = "Flag to indicate whether the cluster should be print in Newick format. This can be useful for display in other programs. " +
            "However, for large datasets a lot of text may be produced, which may not be a nuisance when the Newick format is not required",
            defaultValue = ""))
            @FormDataParam("printNewick") Boolean printNewick,
        @Parameter(schema = @Schema(description = "Sets the number of clusters. If a single hierarchy is desired, set this to 1.",
            defaultValue = "1"))
            @FormDataParam("numClusters") Integer numClusters,
        @Parameter(schema = @Schema(description = "Sets the method used to measure the distance between two clusters.",
            allowableValues = {"SINGLE","COMPLETE","ADJCOMPLETE","AVERAGE","MEAN","CENTROID","WARD","NEIGHBOR_JOINING"}, defaultValue = "SINGLE"))
            @FormDataParam("linkType") String linkType,
        @Parameter(schema = @Schema(description = "If set to false, the distance between clusters is interpreted as the height of the node linking the clusters. " +
            "This is appropriate for example for single link clustering. However, for neighbor joining, the distance is better interpreted as branch length. " +
            "Set this flag to get the latter interpretation.",
            defaultValue = ""))
            @FormDataParam("distanceIsBranchLength") Boolean distanceIsBranchLength,
        @Parameter(schema = @Schema(description = "Sets the distance function, which measures the distance between two individual. " +
            "Instances (or possibly the distance between an instance and the centroid of a clusterdepending on the Link type). " +
            "Examples: 'EuclideanDistance -R first-last', 'weka.core.ChebyshevDistance -R first-last', 'weka.core.ManhattanDistance -R first-last', 'weka.core.MinkowskiDistance -P 2.0 -R first-last' or " +
            "'weka.core.FilteredDistance -R first-last -F \"weka.filters.unsupervised.attribute.RandomProjection -N 10 -R 42 -D Sparse1\" -D \"weka.core.EuclideanDistance -R first-last\"'.",
            defaultValue = ""))
            @FormDataParam("distanceFunction") String distanceFunction,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {
            HashMap<String, Object> params = new HashMap<>();
            params.put("datasetUri", datasetUri);
            params.put("printNewick", printNewick);
            params.put("numClusters", numClusters);
            params.put("linkType", linkType);
            params.put("distanceIsBranchLength", distanceIsBranchLength);
            params.put("distanceFunction", distanceFunction);

            return delegate.clustererPost(fileInputStream, datasetUri, "Hierarchical", params, headers, ui, securityContext);
    }

}
