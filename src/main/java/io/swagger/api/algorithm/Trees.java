package io.swagger.api.algorithm;

import io.swagger.api.AlgorithmService;
import io.swagger.api.NotFoundException;
import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.api.factories.AlgorithmFactory;
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
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task") }),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "J48", value = "https://en.wikipedia.org/wiki/C4.5_algorithm#Implementations")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmJ48Post(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
//J48,
        @Parameter(schema = @Schema(description = "Whether to use binary splits on nominal attributes when building the trees.",
            allowableValues = {"0", "1"}, defaultValue = "0")) @FormDataParam("binarySplits") Integer binarySplits,
        @Parameter(schema = @Schema(description = "The confidence factor used for pruning (smaller values incur more pruning).",
            defaultValue = "0.25")) @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @Parameter(schema = @Schema(description = "The minimum number of instances per leaf.",
            defaultValue = "2")) @FormDataParam("minNumObj") Integer minNumObj,
        @Parameter(schema = @Schema(description = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree",
            defaultValue = "3")) @FormDataParam("numFolds") Integer numFolds,
        @Parameter(schema = @Schema(description = "Whether reduced-error pruning is used instead of C.4.5 pruning.",
            allowableValues = "0, 1", defaultValue = "0")) @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @Parameter(schema = @Schema(description = "The seed used for randomizing the data when reduced-error pruning is used.",
            defaultValue = "1")) @FormDataParam("seed") Integer seed,
        @Parameter(schema = @Schema(description = "Whether to consider the subtree raising operation when pruning.",
            allowableValues = {"0", "1"}, defaultValue = "1")) @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @Parameter(schema = @Schema(description = "Whether pruning is performed.",
            defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("unpruned") Integer unpruned,
        @Parameter(schema = @Schema(description = "Whether counts at leaves are smoothed based on Laplace.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useLaplace") Integer useLaplace,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.", defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description  = "Num of Crossvalidations or Percentage Split %.", defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
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

        return delegate.algorithmPost(fileInputStream, datasetUri,"J48", params,
                                      validation, validationNum, headers, ui, securityContext);
    }


    @POST
    @Path("/J48/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Adaboost M1 with J48 classifier.",
        description = "REST interface to the WEKA Adaboost M1 with J48 classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"} ,
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48/adaboost")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Adaboost M1 meta algorithm", value = "https://en.wikipedia.org/wiki/AdaBoost")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmJ48AdaBoostPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(
            description = "Adaboost M1: The number of iterations to be performed.",
            defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(schema = @Schema(
            description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(schema = @Schema(
            description = "Adaboost M1: Weight threshold for weight pruning.",
            defaultValue = "100")) @FormDataParam("weightThreshold") Integer weightThreshold,
        //J48,
        @Parameter(schema = @Schema(description = "Whether to use binary splits on nominal attributes when building the trees.",
            allowableValues = {"0", "1"}, defaultValue = "0")) @FormDataParam("binarySplits") Integer binarySplits,
        @Parameter(schema = @Schema(description = "The confidence factor used for pruning (smaller values incur more pruning).",
            defaultValue = "0.25")) @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @Parameter(schema = @Schema(description = "The minimum number of instances per leaf.",
            defaultValue = "2")) @FormDataParam("minNumObj") Integer minNumObj,
        @Parameter(schema = @Schema(description = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree",
            defaultValue = "3")) @FormDataParam("numFolds") Integer numFolds,
        @Parameter(schema = @Schema(description = "Whether reduced-error pruning is used instead of C.4.5 pruning.",
            allowableValues = "0, 1", defaultValue = "0")) @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @Parameter(schema = @Schema(description = "The seed used for randomizing the data when reduced-error pruning is used.",
            defaultValue = "1")) @FormDataParam("seed") Integer seed,
        @Parameter(schema = @Schema(description = "Whether to consider the subtree raising operation when pruning.",
            allowableValues = {"0", "1"}, defaultValue = "1")) @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @Parameter(schema = @Schema(description = "Whether pruning is performed.",
            defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("unpruned") Integer unpruned,
        @Parameter(schema = @Schema(description = "Whether counts at leaves are smoothed based on Laplace.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useLaplace") Integer useLaplace,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.", defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description  = "Num of Crossvalidations or Percentage Split %.", defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
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

        return delegate.algorithmPost(fileInputStream, datasetUri,"J48", params,
            "AdaBoost", metaParams, validation, validationNum, headers, ui, securityContext);

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
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
        @Extension(name = "algorithm", properties = {@ExtensionProperty(name = "Bagging meta algorithm", value = "https://en.wikipedia.org/wiki/Bootstrap_aggregating")})
    })
    @GroupedApiResponsesOk
    public Response algorithmJ48BaggingPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Bagging: Size of each bag, as a percentage of the training set size.",
            defaultValue = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(schema = @Schema(description = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Bagging: The number of iterations to be performed.",
            defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        //J48,
        @Parameter(schema = @Schema(description = "Whether to use binary splits on nominal attributes when building the trees.",
            allowableValues = {"0", "1"}, defaultValue = "0")) @FormDataParam("binarySplits") Integer binarySplits,
        @Parameter(schema = @Schema(description = "The confidence factor used for pruning (smaller values incur more pruning).",
            defaultValue = "0.25")) @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @Parameter(schema = @Schema(description = "The minimum number of instances per leaf.",
            defaultValue = "2")) @FormDataParam("minNumObj") Integer minNumObj,
        @Parameter(schema = @Schema(description = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree",
            defaultValue = "3")) @FormDataParam("numFolds") Integer numFolds,
        @Parameter(schema = @Schema(description = "Whether reduced-error pruning is used instead of C.4.5 pruning.",
            allowableValues = "0, 1", defaultValue = "0")) @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @Parameter(schema = @Schema(description = "The seed used for randomizing the data when reduced-error pruning is used.",
            defaultValue = "1")) @FormDataParam("seed") Integer seed,
        @Parameter(schema = @Schema(description = "Whether to consider the subtree raising operation when pruning.",
            allowableValues = {"0", "1"}, defaultValue = "1")) @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @Parameter(schema = @Schema(description = "Whether pruning is performed.",
            defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("unpruned") Integer unpruned,
        @Parameter(schema = @Schema(description = "Whether counts at leaves are smoothed based on Laplace.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useLaplace") Integer useLaplace,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.", defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description  = "Num of Crossvalidations or Percentage Split %.", defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
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

        return delegate.algorithmPost(fileInputStream, datasetUri,"J48", params,
            "Bagging", metaParams, validation, validationNum, headers, ui, securityContext);
    }

    @POST
    @Path("/M5P")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to WEKA M5P classifier.",
        description = "REST interface to WEKA M5P classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"} ,
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/M5P")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "M5P", value = "http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/M5P.html")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmM5PPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        // M5P
        @Parameter(schema = @Schema(description = "Whether unpruned tree to be generated.", example = "0",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("unpruned") Integer unpruned,
        @Parameter(schema = @Schema(description = "Whether to use unsmoothed predictions.",
            defaultValue = "0", allowableValues={"0", "1"}))@FormDataParam("useUnsmoothed") Integer useUnsmoothed,
        @Parameter(schema = @Schema(description = "The minimum number of instances to allow at a leaf node.",
            defaultValue = "4"))@FormDataParam("minNumInstances") Double minNumInstances,
        @Parameter(schema = @Schema(description = "Whether to generate a regression tree/rule instead of a model tree/rule.",
            defaultValue = "0", allowableValues={"0", "1"}))@FormDataParam("buildRegressionTree") Integer buildRegressionTree,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("minNumInstances", minNumInstances);
        params.put("unpruned", unpruned);
        params.put("useUnsmoothed", useUnsmoothed);
        params.put("buildRegressionTree", buildRegressionTree);
        return delegate.algorithmPost(fileInputStream, datasetUri, "M5P", params,
            validation, validationNum, headers, ui, securityContext);
    }

    @POST
    @Path("/M5P/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to WEKA AdaBoost M1 with M5P classifier.",
        description = "REST interface to WEKA AdaBoost M1 with M5P classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"} ,
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/M5P/adaboost")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "M5P", value = "http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/M5P.html")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmM5PAdaBoostPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Adaboost M1: The number of iterations to be performed.",
            defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(schema = @Schema(description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(schema = @Schema(description = "Adaboost M1: Weight threshold for weight pruning.",
            defaultValue = "100"))@FormDataParam("weightThreshold") Integer weightThreshold,
        // M5P
        @Parameter(schema = @Schema(description = "Whether unpruned tree to be generated.", example = "0",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("unpruned") Integer unpruned,
        @Parameter(schema = @Schema(description = "Whether to use unsmoothed predictions.",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("useUnsmoothed") Integer useUnsmoothed,
        @Parameter(schema = @Schema(description = "The minimum number of instances to allow at a leaf node.",
            defaultValue = "4"))@FormDataParam("minNumInstances") Double minNumInstances,
        @Parameter(schema = @Schema(description = "Whether to generate a regression tree/rule instead of a model tree/rule.",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("buildRegressionTree") Integer buildRegressionTree,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        metaParams.put("useResampling", useResampling);
        metaParams.put("weightThreshold", weightThreshold);
        params.put("datasetUri", datasetUri);
        params.put("minNumInstances", minNumInstances);
        params.put("unpruned", unpruned);
        params.put("useUnsmoothed", useUnsmoothed);
        params.put("buildRegressionTree", buildRegressionTree);
        return delegate.algorithmPost(fileInputStream, datasetUri, "M5P", params,
            "AdaBoost", metaParams, validation, validationNum, headers, ui, securityContext);
    }

    @POST
    @Path("/M5P/bagging")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to WEKA Bagging with M5P classifier.",
        description = "REST interface to WEKA Bagging with M5P classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/M5P/bagging")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "M5P", value = "http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/M5P.html")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmM5PBaggingPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Bagging: Size of each bag, as a percentage of the training set size.",
            defaultValue = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(schema = @Schema(description = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Bagging: The number of iterations to be performed.",
            defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        // M5P
        @Parameter(schema = @Schema(description = "Whether unpruned tree to be generated.", example = "0",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("unpruned") Integer unpruned,
        @Parameter(schema = @Schema(description = "Whether to use unsmoothed predictions.",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("useUnsmoothed") Integer useUnsmoothed,
        @Parameter(schema = @Schema(description = "The minimum number of instances to allow at a leaf node.",
            defaultValue = "4"))@FormDataParam("minNumInstances") Double minNumInstances,
        @Parameter(schema = @Schema(description = "Whether to generate a regression tree/rule instead of a model tree/rule.",
            defaultValue = "0", allowableValues = {"0", "1"}))@FormDataParam("buildRegressionTree") Integer buildRegressionTree,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        metaParams.put("bagSizePercent", bagSizePercent);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        params.put("datasetUri", datasetUri);
        params.put("minNumInstances", minNumInstances);
        params.put("unpruned", unpruned);
        params.put("useUnsmoothed", useUnsmoothed);
        params.put("buildRegressionTree", buildRegressionTree);
        return delegate.algorithmPost(fileInputStream, datasetUri, "M5P", params,
            "Bagging", metaParams, validation, validationNum, headers, ui, securityContext);
    }


    @POST
    @Path("/DecisionStump")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA DecisionStump classifier.",
        description = "REST interface to the DecisionStump classifier. " + SAVE_MODEL_NOTE, tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/DecisionStump")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "DecisionStump", value = "https://en.wikipedia.org/wiki/Decision_stump")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmDecisionStumpPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        // DecisionStump
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        return delegate.algorithmPost(fileInputStream, datasetUri, "DecisionStump", params,
            validation, validationNum, headers, ui, securityContext);
    }


    @POST
    @Path("/DecisionStump/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA AdaBoost M1 with DecisionStump classifier.",
        description = "REST interface to the WEKA AdaBoost M1 with DecisionStump classifier. " + SAVE_MODEL_NOTE, 
	tags = {"algorithm","meta algorithm"} ,
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/DecisionStump/adaboost")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "DecisionStump", value = "https://en.wikipedia.org/wiki/Decision_stump")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmDecisionStumpAdaBoostPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Adaboost M1: The number of iterations to be performed.",
            defaultValue = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(schema = @Schema(description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(schema = @Schema(description = "Adaboost M1: Weight threshold for weight pruning.",
            defaultValue = "100")) @FormDataParam("weightThreshold") Integer weightThreshold,
        // DecisionStump
            // validation
            @Parameter(schema = @Schema(description = "Validation to use.",
                allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
            @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
            // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        metaParams.put("useResampling", useResampling);
        metaParams.put("weightThreshold", weightThreshold);
        return delegate.algorithmPost(fileInputStream, datasetUri, "DecisionStump", params,
            "AdaBoost", metaParams, validation, validationNum, headers, ui, securityContext);
    }


    @POST
    @Path("/DecisionStump/bagging")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Bagging with DecisionStump classifier.",
        description = "REST interface to the WEKA Bagging with DecisionStump classifier. " + SAVE_MODEL_NOTE, 
	tags = {"algorithm","meta algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/DecisionStump/bagging")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "DecisionStump", value = "https://en.wikipedia.org/wiki/Decision_stump")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmDecisionStumpBaggingPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Bagging: Size of each bag, as a percentage of the training set size.",
            defaultValue = "100", example = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(schema = @Schema(description = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Bagging: The number of iterations to be performed.",
            defaultValue = "10", example = "10")) @FormDataParam("numIterations") Integer numIterations,
        // DecisionStump
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            allowableValues = {"CrossValidation", "Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        HashMap<String, Object> metaParams = new HashMap<>();
        metaParams.put("bagSizePercent", bagSizePercent);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);

        return delegate.algorithmPost(fileInputStream, datasetUri, "DecisionStump", params,
            "Bagging", metaParams, validation, validationNum, headers, ui, securityContext);
    }


    // RandomForest
    // Class for constructing a forest of random trees.

    @POST
    @Path("/RandomForest")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA RandomForest classifier.",
        description = "REST interface to the WEKA RandomForest classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/algorithm/RandomForest")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Random Forest", value = "https://en.wikipedia.org/wiki/Random_forest")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmRandomForestPost(
        //data params
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //@Parameter(description = "Whether to represent copies of instances using weights rather than explicitly.",
        //    schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("representCopiesUsingWeights ")  representCopiesUsingWeights
        @Parameter(schema = @Schema(description = "Whether to store the out-of-bag predictions.",
            allowableValues =  {"true", "false"}, defaultValue = "false", example = "false")) @FormDataParam("storeOutOfBagPredictions") Boolean storeOutOfBagPredictions,
        @Parameter(schema = @Schema(description = "The number of execution slots (threads) to use for constructing the ensemble.",
            defaultValue = "1", example = "1")) @FormDataParam("numExecutionSlots") Integer numExecutionSlots,
        @Parameter(schema = @Schema(description = "Size of each bag, as a percentage of the training set size.",
            defaultValue = "100", example = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(schema = @Schema(description = "The number of decimal places to be used for the output of numbers in the model.",
            defaultValue = "10", example = "10")) @FormDataParam("numDecimalPlaces") Integer numDecimalPlaces,
        @Parameter(schema = @Schema(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Print the individual classifiers in the output",
            allowableValues =  {"true", "false"}, defaultValue = "false", example = "false")) @FormDataParam("printClassifiers") Boolean printClassifiers,
        @Parameter(schema = @Schema(description = "The number of iterations to be performed.",
            defaultValue = "100", example = "100")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(schema = @Schema(description = "Whether to output complexity-based statistics when out-of-bag evaluation is performed.",
            allowableValues =  {"true", "false"}, defaultValue = "false", example = "false")) @FormDataParam("outputOutOfBagComplexityStatistics") Boolean outputOutOfBagComplexityStatistics,
        @Parameter(schema = @Schema(description = "Break ties randomly when several attributes look equally good.",
            allowableValues =  {"true", "false"}, defaultValue = "false", example = "false")) @FormDataParam("breakTiesRandomly") Boolean breakTiesRandomly,
        @Parameter(schema = @Schema(description = "The maximum depth of the tree, 0 for unlimited.",
            defaultValue = "0", example = "0")) @FormDataParam("maxDepth") Integer maxDepth,
        @Parameter(schema = @Schema(description = "Compute attribute importance via mean impurity decrease",
            allowableValues =  {"true", "false"}, defaultValue = "false", example = "false")) @FormDataParam("computeAttributeImportance") Boolean computeAttributeImportance,
        @Parameter(schema = @Schema(description = "Whether the out-of-bag error is calculated.",
            allowableValues =  {"true", "false"}, defaultValue = "false", example = "false")) @FormDataParam("calcOutOfBag") Boolean calcOutOfBag,
        @Parameter(schema = @Schema(description = "Sets the number of randomly chosen attributes. If 0, int(log_2(#predictors) + 1) is used.",
            defaultValue = "0", example = "0")) @FormDataParam("numFeatures") Integer numFeatures,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            allowableValues = {"CrossValidation", "Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("storeOutOfBagPredictions", storeOutOfBagPredictions);
        params.put("numExecutionSlots", numExecutionSlots);
        params.put("bagSizePercent", bagSizePercent);
        params.put("numDecimalPlaces", numDecimalPlaces);
        params.put("batchSize", batchSize);
        params.put("printClassifiers", printClassifiers);
        params.put("numIterations", numIterations);
        params.put("outputOutOfBagComplexityStatistics", outputOutOfBagComplexityStatistics);
        params.put("breakTiesRandomly", breakTiesRandomly);
        params.put("maxDepth", maxDepth);
        params.put("computeAttributeImportance", computeAttributeImportance);
        params.put("calcOutOfBag", calcOutOfBag);
        params.put("numFeatures", numFeatures);

        return delegate.algorithmPost(fileInputStream, datasetUri, "RandomForest", params,
            validation, validationNum, headers, ui, securityContext);
    }

}