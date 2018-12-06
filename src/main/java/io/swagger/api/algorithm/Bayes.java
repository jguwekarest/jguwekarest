package io.swagger.api.algorithm;

import io.swagger.api.AlgorithmService;
import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.api.annotations.GroupedValidationParameters;
import io.swagger.api.factories.AlgorithmFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;
import static io.swagger.api.Constants.TEXT_URILIST;



@Path("/algorithm")

/**
 * Webservice Routes to Bayes Classifiers and
 * Annotations to the Bayes Algorithm API
 */
public class Bayes {
    private final AlgorithmService delegate;

    public Bayes(@Context ServletConfig servletContext) {
        AlgorithmService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Algorithm.implementation");
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


    /**
     * REST interface to BayesNet algorithm
     */

    @POST @Path("/BayesNet")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA BayesNet classifier.", description = "REST interface to the WEKA BayesNet classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk
    @GroupedValidationParameters
    public Response algorithmBayesNetPost(
        //@FormDataParam("file") InputStream fileInputStream,
        //@FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).", example = "")
            ) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        @Parameter(schema = @Schema(description = "The estimator algorithm to be used in the compound. Must be SimpleEstimator,  MultiNomialBMAEstimator, BMAEstimator or BayesNetEstimator (Default: SimpleEstimator).",
            allowableValues={"SimpleEstimator", "MultiNomialBMAEstimator", "BMAEstimator", "BayesNetEstimator"},
            defaultValue="SimpleEstimator",example="SimpleEstimator")) @DefaultValue("SimpleEstimator") @FormDataParam("estimator") String estimator,
        //schema = @Schema(  allowableValues="SimpleEstimator, MultiNomialBMAEstimator, BMAEstimator, BayesNetEstimator", defaultValue="SimpleEstimator", type = "string", name = "estimator")
        @Parameter(schema = @Schema(description = "The parameter for the estimator to be used in the compound.  Must be of type double (Default: 0.5).",
            defaultValue="0.5", example="0.5"),
            extensions = {@Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")})})@FormDataParam("estimatorParams") BigDecimal estimatorParams,
        @Parameter(schema = @Schema(description = "Whether to use ADTrees for searching (using will increase the speed of the search, but will also raise the memory use (Default: 0).",
            defaultValue="0", allowableValues = {"0", "1"}))@DefaultValue("0") @FormDataParam("useADTree") Integer useADTree,
        @Parameter(schema = @Schema(description = "The algorithmn to be used for searching in the compound. Must be local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm (Default: local.K2).",
            allowableValues={"local.K2", "local.GeneticSearch", "local.HillClimber", "local.LAGDHillClimber", "local.RepeatedHillClimber", "local.SimulatedAnnealing", "local.TabuSearch", "local.TAN", "global.K2", "global.GeneticSearch", "global.HillClimber", "global.RepeatedHillClimber", "global.SimulatedAnnealing", "global.TabuSearch", "global.TAN", "ci.CISearchAlgorithm", "ci.ICSSearchAlgorithm"},
            defaultValue="local.K2"), example = "local.K2") @DefaultValue("local.K2") @FormDataParam("searchAlgorithm") String searchAlgorithm,
        @Parameter(schema = @Schema(description = "The parameter for algorithmn to be used for searching in the compound. Are set automatically (WEKA's standard parameter setting) (Default '-P 1 -S BAYES' for local.K2).",
            defaultValue="-P 1 -S BAYES",example = "-P 1 -S BAYES"))@FormDataParam("searchParams") String searchParams,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            defaultValue="CrossValidation", example="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @DefaultValue("CrossValidation") @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", minimum = "0",example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)

        throws io.swagger.api.NotFoundException, IOException {

                HashMap<String, Object> params = new HashMap<>();
                params.put("datasetUri", datasetUri);
                params.put("estimator", estimator);
                params.put("estimatorParams", estimatorParams);
                params.put("useADTree", useADTree);
                params.put("searchAlgorithm", searchAlgorithm);
                params.put("searchParams", searchParams);

                return delegate.algorithmPost(fileInputStream, datasetUri, "BayesNet", params,
                                              validation, validationNum, headers, ui, securityContext);
    }




    /**
     * REST interface to BayesNet in AdaBoost M1 meta algorithm
     */
    @POST
    @Path("/BayesNet/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA AdaBoost M1 with BayesNet classifier.", description = "REST interface to the WEKA AdaBoost M1 with BayesNet classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm","meta algorithm" }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet/adaboost")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk
    public Response algorithmBayesNetAdaBoostPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params,
        @Parameter(schema = @Schema(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @DefaultValue("100") @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Adaboost M1: The number of iterations to be performed.",
            defaultValue = "10", example = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(schema = @Schema(description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(schema = @Schema(description = "Adaboost M1: Weight threshold for weight pruning.",
            defaultValue = "100", example = "100")) @FormDataParam("weightThreshold") Integer weightThreshold,
        //BayesNet params
        @Parameter(schema = @Schema(description = "The estimator algorithm to be used in the compound. Must be SimpleEstimator,  MultiNomialBMAEstimator, BMAEstimator or BayesNetEstimator (Default: SimpleEstimator).",
            allowableValues={"SimpleEstimator", "MultiNomialBMAEstimator", "BMAEstimator", "BayesNetEstimator"},
            defaultValue="SimpleEstimator",example="SimpleEstimator")) @DefaultValue("SimpleEstimator") @FormDataParam("estimator") String estimator,
        @Parameter(schema = @Schema(description = "The parameter for the estimator to be used in the compound.  Must be of type double (Default: 0.5).",
            defaultValue="0.5", example="0.5"),
            extensions = {@Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")})})@FormDataParam("estimatorParams") BigDecimal estimatorParams,
        @Parameter(schema = @Schema(description = "Whether to use ADTrees for searching (using will increase the speed of the search, but will also raise the memory use (Default: 0).",
            defaultValue="0", allowableValues = {"0", "1"})) @DefaultValue("0") @FormDataParam("useADTree") Integer useADTree,
        @Parameter(schema = @Schema(description = "The algorithmn to be used for searching in the compound. Must be local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm (Default: local.K2).",
            allowableValues={"local.K2", "local.GeneticSearch", "local.HillClimber", "local.LAGDHillClimber", "local.RepeatedHillClimber", "local.SimulatedAnnealing", "local.TabuSearch", "local.TAN", "global.K2", "global.GeneticSearch", "global.HillClimber", "global.RepeatedHillClimber", "global.SimulatedAnnealing", "global.TabuSearch", "global.TAN", "ci.CISearchAlgorithm", "ci.ICSSearchAlgorithm"},
            defaultValue="local.K2"), example = "local.K2") @DefaultValue("local.K2") @FormDataParam("searchAlgorithm") String searchAlgorithm,
        @Parameter(schema = @Schema(description = "The parameter for algorithmn to be used for searching in the compound. Are set automatically (WEKA's standard parameter setting) (Default '-P 1 -S BAYES' for local.K2).",
            defaultValue="-P 1 -S BAYES",example = "-P 1 -S BAYES"))@FormDataParam("searchParams") String searchParams,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            defaultValue="CrossValidation", example="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @DefaultValue("CrossValidation") @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.",
            defaultValue = "10", minimum = "0",example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("estimator", estimator);
        params.put("estimatorParams", estimatorParams);
        params.put("useADTree", useADTree);
        params.put("searchAlgorithm", searchAlgorithm);
        params.put("searchParams", searchParams);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        metaParams.put("useResampling", useResampling);
        metaParams.put("weightThreshold", weightThreshold);

        return delegate.algorithmPost(fileInputStream, datasetUri, "BayesNet", params,
                    "AdaBoost", metaParams, validation, validationNum, headers, ui, securityContext);
    }


    /**
     * REST interface to BayesNet in Bagging meta algorithm
     */
    @POST
    @Path("/BayesNet/bagging")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Bagging with BayesNet classifier.", description = "REST interface to the WEKA Bagging with BayesNet BayesNet classifier. " + SAVE_MODEL_NOTE,
        tags={ "algorithm","meta algorithm" }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet/adaboost")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk
    public Response algorithmBayesNetBaggingPost(
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
        //BayesNet params
        @Parameter(schema = @Schema(description = "The estimator algorithm to be used in the compound. Must be SimpleEstimator,  MultiNomialBMAEstimator, BMAEstimator or BayesNetEstimator (Default: SimpleEstimator).",
            allowableValues={"SimpleEstimator", "MultiNomialBMAEstimator", "BMAEstimator", "BayesNetEstimator"},
            defaultValue="SimpleEstimator",example="SimpleEstimator")) @DefaultValue("SimpleEstimator") @FormDataParam("estimator") String estimator,
        @Parameter(schema = @Schema(description = "The parameter for the estimator to be used in the compound.  Must be of type double (Default: 0.5).",
            defaultValue="0.5", example="0.5"),
            extensions = {@Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")})})@FormDataParam("estimatorParams") BigDecimal estimatorParams,
        @Parameter(schema = @Schema(description = "Whether to use ADTrees for searching (using will increase the speed of the search, but will also raise the memory use (Default: 0).",
            defaultValue="0", allowableValues = {"0", "1"}))@DefaultValue("0") @FormDataParam("useADTree") Integer useADTree,
        @Parameter(schema = @Schema(description = "The algorithmn to be used for searching in the compound. Must be local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm (Default: local.K2).",
            allowableValues={"local.K2", "local.GeneticSearch", "local.HillClimber", "local.LAGDHillClimber", "local.RepeatedHillClimber", "local.SimulatedAnnealing", "local.TabuSearch", "local.TAN", "global.K2", "global.GeneticSearch", "global.HillClimber", "global.RepeatedHillClimber", "global.SimulatedAnnealing", "global.TabuSearch", "global.TAN", "ci.CISearchAlgorithm", "ci.ICSSearchAlgorithm"},
            defaultValue="local.K2"), example = "local.K2") @DefaultValue("local.K2") @FormDataParam("searchAlgorithm") String searchAlgorithm,
        @Parameter(schema = @Schema(description = "The parameter for algorithmn to be used for searching in the compound. Are set automatically (WEKA's standard parameter setting) (Default '-P 1 -S BAYES' for local.K2).",
            defaultValue="-P 1 -S BAYES",example = "-P 1 -S BAYES"))@FormDataParam("searchParams") String searchParams,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.",
            defaultValue="CrossValidation", example="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @DefaultValue("CrossValidation") @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", minimum = "0",example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        metaParams.put("bagSizePercent", bagSizePercent);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        params.put("datasetUri", datasetUri);
        params.put("estimator", estimator);
        params.put("estimatorParams", estimatorParams);
        params.put("useADTree", useADTree);
        params.put("searchAlgorithm", searchAlgorithm);
        params.put("searchParams", searchParams);

        return delegate.algorithmPost(fileInputStream, datasetUri, "BayesNet", params,
                    "Bagging", metaParams, validation, validationNum, headers, ui, securityContext);
    }


    /**
     * REST interface to NaiveBayes algorithm
     */
    @POST
    @Path("/NaiveBayes")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA BayesNet classifier.", description = "REST interface to the WEKA BayesNet classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk

    public Response algorithmNaiveBayesPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        @Parameter(schema = @Schema(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Use a kernel estimator for numeric attributes rather than a normal distribution. (Default: 0).",
            allowableValues={"0","1"}, defaultValue="0"))@FormDataParam("useKernelEstimator") Integer useKernelEstimator,
        @Parameter(schema = @Schema(description = "Use supervised discretization to convert numeric attributes to nominal ones. (Default: 0). Works not together with useKernelEstimator=1.",
            allowableValues={"0","1"}, defaultValue="0"))@FormDataParam("useSupervisedDiscretization") BigDecimal useSupervisedDiscretization,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.", defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", minimum = "0",example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("batchSize", batchSize);
        params.put("useKernelEstimator", useKernelEstimator);
        params.put("useSupervisedDiscretization", useSupervisedDiscretization);

        return delegate.algorithmPost(fileInputStream, datasetUri, "NaiveBayes", params,
                                      validation, validationNum, headers, ui, securityContext);
    }



    /**
     * REST interface to NaiveBayes algorithm
     */
    @POST
    @Path("/NaiveBayes/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA  AdaBoost M1 with NaiveBayes classifier.", description = "REST interface to the WEKA  AdaBoost M1 with NaiveBayes classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk

    public Response algorithmNaiveBayesAdaBoostPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params
        @Parameter(schema = @Schema(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSizeAda,
        @Parameter(schema = @Schema(description = "Adaboost M1: The number of iterations to be performed.",
            defaultValue = "10", example = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(schema = @Schema(description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(schema = @Schema(description = "Adaboost M1: Weight threshold for weight pruning.",
            defaultValue = "100", example = "100")) @FormDataParam("weightThreshold") Integer weightThreshold,
        //NaiveBayes params
        @Parameter(schema = @Schema(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Use a kernel estimator for numeric attributes rather than a normal distribution. (Default: 0).",
            allowableValues={"0","1"}, defaultValue="0"))@FormDataParam("useKernelEstimator")  Integer useKernelEstimator,
        @Parameter(schema = @Schema(description = "Use supervised discretization to convert numeric attributes to nominal ones. (Default: 0). Works not together with useKernelEstimator=1.",
            allowableValues={"0","1"}, defaultValue="0"))@FormDataParam("useSupervisedDiscretization") BigDecimal useSupervisedDiscretization,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.", defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", minimum = "0",example = "10")) @FormDataParam("validationNum") Double validationNum,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("batchSize", batchSize);
        params.put("useKernelEstimator", useKernelEstimator);
        params.put("useSupervisedDiscretization", useSupervisedDiscretization);
        metaParams.put("batchSize", batchSizeAda);
        metaParams.put("numIterations", numIterations);
        metaParams.put("useResampling", useResampling);
        metaParams.put("weightThreshold", weightThreshold);

        return delegate.algorithmPost(fileInputStream, datasetUri, "NaiveBayes", params,
            "AdaBoost", metaParams, validation, validationNum, headers, ui, securityContext);
    }



    /**
     * REST interface to NaiveBayes algorithm
     */
    @POST
    @Path("/NaiveBayes/bagging")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Bagging with NaiveBayes classifier.", description = "REST interface to the WEKA Bagging with NaiveBayes classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk

    public Response algorithmNaiveBayesBaggingPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(schema = @Schema(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).",
            defaultValue = "", example = "")) @DefaultValue("") @FormDataParam("datasetUri") String datasetUri,
        //meta params
        @Parameter(schema = @Schema(description = "Bagging: Size of each bag, as a percentage of the training set size.",
            defaultValue = "100", example = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(schema = @Schema(description = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSizeBagging,
        @Parameter(schema = @Schema(description = "Bagging: The number of iterations to be performed.",
            defaultValue = "10", example = "10")) @FormDataParam("numIterations") Integer numIterations,
        //NaiveBayes params
        @Parameter(schema = @Schema(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(schema = @Schema(description = "Use a kernel estimator for numeric attributes rather than a normal distribution. (Default: 0).",
            allowableValues={"0","1"}, defaultValue="0")) @FormDataParam("useKernelEstimator")  Integer useKernelEstimator,
        @Parameter(schema = @Schema(description = "Use supervised discretization to convert numeric attributes to nominal ones. (Default: 0). Works not together with useKernelEstimator=1.",
            allowableValues={"0","1"}, defaultValue="0")) @FormDataParam("useSupervisedDiscretization") BigDecimal useSupervisedDiscretization,
        // validation
        @Parameter(schema = @Schema(description = "Validation to use.", defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(schema = @Schema(description = "Num of Crossvalidations or Percentage Split %.", defaultValue = "10", minimum = "0",example = "10")) @FormDataParam("validationNum") Double validationNum,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("batchSize", batchSize);
        params.put("useKernelEstimator", useKernelEstimator);
        params.put("useSupervisedDiscretization", useSupervisedDiscretization);
        metaParams.put("bagSizePercent", bagSizePercent);
        metaParams.put("batchSize", batchSizeBagging);
        metaParams.put("numIterations", numIterations);

        return delegate.algorithmPost(fileInputStream, datasetUri, "NaiveBayes", params,
            "Bagging", metaParams, validation, validationNum, headers, ui, securityContext);
    }
}

