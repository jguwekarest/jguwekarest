package io.swagger.api.algorithm;

import io.swagger.api.AlgorithmService;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.AlgorithmFactory;
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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;
import static io.swagger.api.Constants.TEXT_URILIST;


@Path("/algorithm")

//@Api(description = "Trees algorithm API")

public class Trees  {
    private final AlgorithmService delegate;

    public Trees(@Context ServletConfig servletContext) {
        AlgorithmService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("AlgorithmApi.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (AlgorithmService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = AlgorithmFactory.getAlgorithm();
        }
        this.delegate = delegate;
    }


    @Context ServletContext servletContext;
    @POST
    @Path("/J48")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA J48 classifier.",
        description = "REST interface to the WEKA J48 classifier. " + SAVE_MODEL_NOTE,
        tags={ "algorithm" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset") }),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model") }),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "J48", value = "https://en.wikipedia.org/wiki/C4.5_algorithm#Implementations")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found") })
    public Response algorithmJ48Post(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
//J48,
        @Parameter(description = "Whether to use binary splits on nominal attributes when building the trees.",
            schema = @Schema(allowableValues = {"0", "1"}, defaultValue = "0")) @FormDataParam("binarySplits") Integer binarySplits,
        @Parameter(description = "The confidence factor used for pruning (smaller values incur more pruning).",
            schema = @Schema(defaultValue = "0.25")) @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @Parameter(description = "The minimum number of instances per leaf.",
            schema = @Schema(defaultValue = "2")) @FormDataParam("minNumObj") Integer minNumObj,
        @Parameter(description = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree",
            schema = @Schema(defaultValue = "3")) @FormDataParam("numFolds") Integer numFolds,
        @Parameter(description = "Whether reduced-error pruning is used instead of C.4.5 pruning.",
            schema = @Schema(allowableValues = "0, 1", defaultValue = "0")) @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @Parameter(description = "The seed used for randomizing the data when reduced-error pruning is used.",
            schema = @Schema(defaultValue = "1")) @FormDataParam("seed") Integer seed,
        @Parameter(description = "Whether to consider the subtree raising operation when pruning.",
            schema = @Schema(allowableValues = {"0", "1"}, defaultValue = "1")) @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @Parameter(description = "Whether pruning is performed.",
            schema = @Schema(defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("unpruned") Integer unpruned,
        @Parameter(description = "Whether counts at leaves are smoothed based on Laplace.",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useLaplace") Integer useLaplace,
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("binarySplits", binarySplits);
        params.put("confidenceFactor", confidenceFactor);
        params.put("minNumObj", minNumObj);
        params.put("numFolds", numFolds);
        params.put("reducedErrorPruning", reducedErrorPruning);
        params.put("seed", seed);
        params.put("subtreeRaising", subtreeRaising);
        params.put("unpruned", unpruned);
        params.put("useLaplace", useLaplace);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri,"J48", params,
                                      headers, ui, securityContext);
    }


    @POST
    @Path("/J48/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Adaboost M1 meta classifier.",
        description = "REST interface to the WEKA Adaboost M1 meta classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"} ,
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48/adaboost")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Adaboost M1 meta algorithm", value = "https://en.wikipedia.org/wiki/AdaBoost")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found")})
    public Response algorithmJ48AdaBoostPost(
        //data params
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,@Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).") @FormDataParam("datasetURI") String datasetUri,
        //meta params,
        @Parameter(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(
            description = "Adaboost M1: The number of iterations to be performed.",
            schema = @Schema(defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(
            description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(
            description = "Adaboost M1: Weight threshold for weight pruning.",
            schema = @Schema(defaultValue = "100")) @FormDataParam("weightThreshold") Integer weightThreshold,
        //J48,
        @Parameter(description = "Whether to use binary splits on nominal attributes when building the trees.",
            schema = @Schema(allowableValues = {"0", "1"}, defaultValue = "0")) @FormDataParam("binarySplits") Integer binarySplits,
        @Parameter(description = "The confidence factor used for pruning (smaller values incur more pruning).",
            schema = @Schema(defaultValue = "0.25")) @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @Parameter(description = "The minimum number of instances per leaf.",
            schema = @Schema(defaultValue = "2")) @FormDataParam("minNumObj") Integer minNumObj,
        @Parameter(description = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree",
            schema = @Schema(defaultValue = "3")) @FormDataParam("numFolds") Integer numFolds,
        @Parameter(description = "Whether reduced-error pruning is used instead of C.4.5 pruning.",
            schema = @Schema(allowableValues = "0, 1", defaultValue = "0")) @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @Parameter(description = "The seed used for randomizing the data when reduced-error pruning is used.",
            schema = @Schema(defaultValue = "1")) @FormDataParam("seed") Integer seed,
        @Parameter(description = "Whether to consider the subtree raising operation when pruning.",
            schema = @Schema(allowableValues = {"0", "1"}, defaultValue = "1")) @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @Parameter(description = "Whether pruning is performed.",
            schema = @Schema(defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("unpruned") Integer unpruned,
        @Parameter(description = "Whether counts at leaves are smoothed based on Laplace.",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useLaplace") Integer useLaplace,
        //general params,
        @Parameter(description = "Authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("binarySplits", binarySplits);
        params.put("confidenceFactor", confidenceFactor);
        params.put("minNumObj", minNumObj);
        params.put("numFolds", numFolds);
        params.put("reducedErrorPruning", reducedErrorPruning);
        params.put("seed", seed);
        params.put("subtreeRaising", subtreeRaising);
        params.put("unpruned", unpruned);
        params.put("useLaplace", useLaplace);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        metaParams.put("useResampling", useResampling);
        metaParams.put("weightThreshold", weightThreshold);


        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri,"J48", params,
            "AdaBoost", metaParams, headers, ui, securityContext);

    }


    @POST
    @Path("/J48/bagging")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Bagging meta classifier.",
        description = "REST interface to the WEKA Bagging meta classifier. " + SAVE_MODEL_NOTE, tags = {"algorithm","meta algorithm"},
        extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48/bagging")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
        @Extension(name = "algorithm", properties = {@ExtensionProperty(name = "Bagging meta algorithm", value = "https://en.wikipedia.org/wiki/Bootstrap_aggregating")})
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found")})
    public Response algorithmJ48BaggingPost(
        //data params
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).") @FormDataParam("datasetURI") String datasetUri,
        //meta params,
        @Parameter(description = "Bagging: Size of each bag, as a percentage of the training set size.",
            schema = @Schema(defaultValue = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(description = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(description = "Bagging: The number of iterations to be performed.",
            schema = @Schema(defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        //J48,
        @Parameter(description = "Whether to use binary splits on nominal attributes when building the trees.",
            schema = @Schema(allowableValues = {"0", "1"}, defaultValue = "0")) @FormDataParam("binarySplits") Integer binarySplits,
        @Parameter(description = "The confidence factor used for pruning (smaller values incur more pruning).",
            schema = @Schema(defaultValue = "0.25")) @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @Parameter(description = "The minimum number of instances per leaf.",
            schema = @Schema(defaultValue = "2")) @FormDataParam("minNumObj") Integer minNumObj,
        @Parameter(description = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree",
            schema = @Schema(defaultValue = "3")) @FormDataParam("numFolds") Integer numFolds,
        @Parameter(description = "Whether reduced-error pruning is used instead of C.4.5 pruning.",
            schema = @Schema(allowableValues = "0, 1", defaultValue = "0")) @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @Parameter(description = "The seed used for randomizing the data when reduced-error pruning is used.",
            schema = @Schema(defaultValue = "1")) @FormDataParam("seed") Integer seed,
        @Parameter(description = "Whether to consider the subtree raising operation when pruning.",
            schema = @Schema(allowableValues = {"0", "1"}, defaultValue = "1")) @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @Parameter(description = "Whether pruning is performed.",
            schema = @Schema(defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("unpruned") Integer unpruned,
        @Parameter(description = "Whether counts at leaves are smoothed based on Laplace.",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useLaplace") Integer useLaplace,
        //general params,
        @Parameter(description = "Authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        metaParams.put("bagSizePercent", bagSizePercent);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        params.put("datasetUri", datasetUri);
        params.put("binarySplits", binarySplits);
        params.put("confidenceFactor", confidenceFactor);
        params.put("minNumObj", minNumObj);
        params.put("numFolds", numFolds);
        params.put("reducedErrorPruning", reducedErrorPruning);
        params.put("seed", seed);
        params.put("subtreeRaising", subtreeRaising);
        params.put("unpruned", unpruned);
        params.put("useLaplace", useLaplace);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri,"J48", params,
            "Bagging", metaParams, headers, ui, securityContext);
    }

}