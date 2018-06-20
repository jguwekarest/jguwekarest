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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
//@Api(description = "the functions algorithm API")

public class Functions {

    private final AlgorithmService delegate;

    public Functions(@Context ServletConfig servletContext) {
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


    @POST
    @Path("/linearRegression")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA linear regression classifier.",
        description = "REST interface to the WEKA linear regression classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/linearRegression")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Linear Regression", value = "https://en.wikipedia.org/wiki/Linear_regression")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmLRPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        @Parameter(description = "Attribute selection method to be used (Default M5 method).Available methods are: no attribute selection(Value:1), attribute selection using M5's method (Value:0) and a greedy selection using the Akaike information metric(Value:2). One of 0,1,2 (Default: 0).",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1", "2"}) ) @FormDataParam("attributeSelectionMethod") Integer attributeSelectionMethod,
        @Parameter(description = "Whether to eliminate colinear attributes. Must be 0 or 1 (Default: 1).",
            schema = @Schema(defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("eliminateColinearAttributes") Integer eliminateColinearAttributes,
        @Parameter(description = "The ridge parameter (Default: 1.0E-8).",
            schema = @Schema(defaultValue = "1.0E-8")) @FormDataParam("ridge") BigDecimal ridge,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

            HashMap<String, Object> params = new HashMap<>();
            params.put("datasetUri", datasetUri);
            params.put("attributeSelectionMethod", attributeSelectionMethod);
            params.put("eliminateColinearAttributes", eliminateColinearAttributes);
            params.put("ridge", ridge);

            return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "LinearRegression", params,
                                          validation, validationNum, headers, ui, securityContext);
    }


    @POST
    @Path("/linearRegression/adaboost")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Bagging with linear regression classifier.",
        description = "REST interface to the WEKA Bagging with linear regression classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/linearRegression/adaboost")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Linear Regression", value = "https://en.wikipedia.org/wiki/Linear_regression")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmLRAdaBoostPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        //meta params
        @Parameter(description = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100", example = "100")) @DefaultValue("100") @FormDataParam("batchSize") Integer batchSize,
        @Parameter(
            description = "Adaboost M1: The number of iterations to be performed.",
            schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("numIterations") Integer numIterations,
        @Parameter(
            description = "Adaboost M1: Whether resampling is used instead of reweighting.",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1"})) @FormDataParam("useResampling") Integer useResampling,
        @Parameter(
            description = "Adaboost M1: Weight threshold for weight pruning.",
            schema = @Schema(defaultValue = "100", example = "100")) @FormDataParam("weightThreshold") Integer weightThreshold,
        // LR params
        @Parameter(description = "Attribute selection method to be used (Default M5 method).Available methods are: no attribute selection(Value:1), attribute selection using M5's method (Value:0) and a greedy selection using the Akaike information metric(Value:2). One of 0,1,2 (Default: 0).",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1", "2"}) ) @FormDataParam("attributeSelectionMethod") Integer attributeSelectionMethod,
        @Parameter(description = "Whether to eliminate colinear attributes. Must be 0 or 1 (Default: 1).",
            schema = @Schema(defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("eliminateColinearAttributes") Integer eliminateColinearAttributes,
        @Parameter(description = "The ridge parameter (Default: 1.0E-8).",
            schema = @Schema(defaultValue = "1.0E-8")) @FormDataParam("ridge") BigDecimal ridge,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("attributeSelectionMethod", attributeSelectionMethod);
        params.put("eliminateColinearAttributes", eliminateColinearAttributes);
        params.put("ridge", ridge);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);
        metaParams.put("useResampling", useResampling);
        metaParams.put("weightThreshold", weightThreshold);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "LinearRegression", params,
            "AdaBoost", metaParams, validation, validationNum, headers, ui, securityContext);
    }

    @POST
    @Path("/linearRegression/bagging")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Bagging with linear regression classifier.",
        description = "REST interface to the WEKA linear Bagging with regression classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/linearRegression/bagging")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Linear Regression", value = "https://en.wikipedia.org/wiki/Linear_regression")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmLRBaggingPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        //meta params
        @Parameter(description = "Bagging: Size of each bag, as a percentage of the training set size.",
            schema = @Schema(defaultValue = "100", example = "100")) @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @Parameter(description = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100", example = "100")) @FormDataParam("batchSize") Integer batchSize,
        @Parameter(description = "Bagging: The number of iterations to be performed.",
            schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("numIterations") Integer numIterations,
        // LR params
        @Parameter(description = "Attribute selection method to be used (Default M5 method).Available methods are: no attribute selection(Value:1), attribute selection using M5's method (Value:0) and a greedy selection using the Akaike information metric(Value:2). One of 0,1,2 (Default: 0).",
            schema = @Schema(defaultValue = "0", allowableValues = {"0", "1", "2"}) ) @FormDataParam("attributeSelectionMethod") Integer attributeSelectionMethod,
        @Parameter(description = "Whether to eliminate colinear attributes. Must be 0 or 1 (Default: 1).",
            schema = @Schema(defaultValue = "1", allowableValues = {"0", "1"})) @FormDataParam("eliminateColinearAttributes") Integer eliminateColinearAttributes,
        @Parameter(description = "The ridge parameter (Default: 1.0E-8).",
            schema = @Schema(defaultValue = "1.0E-8")) @FormDataParam("ridge") BigDecimal ridge,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        HashMap<String, Object> metaParams = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("attributeSelectionMethod", attributeSelectionMethod);
        params.put("eliminateColinearAttributes", eliminateColinearAttributes);
        params.put("ridge", ridge);
        metaParams.put("bagSizePercent", bagSizePercent);
        metaParams.put("batchSize", batchSize);
        metaParams.put("numIterations", numIterations);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "LinearRegression", params,
            "Bagging", metaParams, validation, validationNum, headers, ui, securityContext);
    }

    @POST
    @Path("/libsvm")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA support vector machine wrapper library classifier.",
        description = "REST interface to the WEKA support vector machine wrapper library classifier." + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/libsvm")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "support vector machine", value = "https://en.wikipedia.org/wiki/Support_vector_machine")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmLibSVMPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        @Parameter(description = "SVMType -- The type of SVM to use. \n 0: C-SVC (classification) \n 1: nu-SVC (classification) \n 2: one-class SVM (classification) \n 3: epsilon-SVR (regression)\n 4: nu-SVR (regression)\n (Default: 0).",
            schema = @Schema(defaultValue = "0", allowableValues = {"0","1","2","3","4"} )) @FormDataParam("svmType") Integer svmType,
        @Parameter(description = "coef0 -- The coefficient to use. (Default: 0).",
            schema = @Schema(defaultValue = "0")) @FormDataParam("coef0") Float coef0,
        @Parameter(description = "cost -- The cost parameter C for C-SVC, epsilon-SVR and nu-SVR. (Default: 1.0).",
            schema = @Schema(defaultValue = "1.0")) @FormDataParam("cost") Float cost,
        @Parameter(description = "degree -- The degree of the kernel. (Default: 3).",
            schema = @Schema(defaultValue = "3")) @FormDataParam("degree") Integer degree,
        @Parameter(description = "eps -- The tolerance of the termination criterion. (Default: 0.001).",
            schema = @Schema(defaultValue = "0.001")) @FormDataParam("eps") BigDecimal eps,
        @Parameter(description = "gamma -- The gamma to use, if 0 then 1/max_index is used. (Default: 0.0).",
            schema = @Schema(defaultValue = "0.0")) @FormDataParam("gamma") BigDecimal gamma,
        @Parameter(description = "kernelType -- The type of kernel to use.\n 0: linear:u'*v \n 1: polynomial: (gamma*u'*v + coef0)^degree \n 2: radial basis function: exp(-gamma*|u-v|^2) \n 3: sigmoid: tanh()gamma*u'*v + coef0) \n (Default: 2).",
            schema = @Schema(defaultValue = "2", allowableValues = {"0","1","2","3"} )) @FormDataParam("kernelType") Integer kernelType,
        @Parameter(description = "loss -- The epsilon for the loss function in epsilon-SVR. (Default: 0.1).",
            schema = @Schema(defaultValue = "0.1")) @FormDataParam("loss") BigDecimal loss,
        @Parameter(description = "normalize -- Whether to normalize the data.",
            schema = @Schema(defaultValue = "false")) @FormDataParam("normalize") Boolean normalize,
        @Parameter(description = "nu -- The value of nu for nu-SVC, one-class SVM and nu-SVR. (Default: 0.5).",
            schema = @Schema(defaultValue = "0.5")) @FormDataParam("nu") BigDecimal nu,
        @Parameter(description = "probabilityEstimates -- Whether to generate probability estimates instead of -1/+1 for classification problems.",
            schema = @Schema(defaultValue = "false")) @FormDataParam("probabilityEstimates") Boolean probabilityEstimates,
        @Parameter(description = "shrinking -- Whether to use the shrinking heuristic.",
            schema = @Schema(defaultValue = "true")) @FormDataParam("shrinking") Boolean shrinking,
        @Parameter(description = "weights -- The weights to use for the classes (blank-separated list, eg, \"1 1 1\" for a 3-class problem), if empty 1 is used by default.") @FormDataParam("weights") String weights,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

            HashMap<String, Object> params = new HashMap<>();
            params.put("datasetUri", datasetUri);
            params.put("svmType", svmType);
            params.put("coef0", coef0);
            params.put("cost", cost);
            params.put("degree", degree);
            params.put("eps", eps);
            params.put("gamma", gamma);
            params.put("kernelType", kernelType);
            params.put("loss", loss);
            params.put("normalize", normalize);
            params.put("nu", nu);
            params.put("probabilityEstimates", probabilityEstimates);
            params.put("shrinking", shrinking);
            params.put("weights", weights);
            //svmType, coef0, cost, degree, eps, gamma, kernelType, loss, normalize, nu, probabilityEstimates, shrinking, weights
            return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "LibSVM", params,
                                          validation, validationNum, headers, ui, securityContext);
    }

    @POST
    @Path("/logistic")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA logistic classifier.",
        description = "REST interface to the WEKA logistic classifier. Logistic class for building and using a multinomial logistic regression model with a ridge estimator. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/logistic")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "support vector machine", value = "https://en.wikipedia.org/wiki/Multinomial_logistic_regression")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmLogisticPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        // logistic params
        @Parameter(description = "Set the Ridge value in the log-likelihood.") @FormDataParam("ridge") BigDecimal ridge,
        @Parameter(description = "Use conjugate gradient descent rather than BFGS updates; faster for problems with many parameters.") @FormDataParam("useConjugateGradientDescent") Boolean useConjugateGradientDescent,
        @Parameter(description = "Maximum number of iterations to perform.") @FormDataParam("maxIts") Integer maxIts,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(description = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue = "10",example = "10")) @FormDataParam("validationNum") Double validationNum,
        // headers
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("ridge", ridge);
        params.put("useConjugateGradientDescent", useConjugateGradientDescent);
        params.put("maxIts", maxIts);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "Logistic", params,
                                      validation, validationNum, headers, ui, securityContext);
    }

    //MultilayerPerceptron

    @POST
    @Path("/MultilayerPerceptron")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA logistic classifier.",
        description = "REST interface to the WEKA logistic classifier. Logistic class for building and using a multinomial logistic regression model with a ridge estimator. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/MultilayerPerceptron")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Multilayer Perceptron", value = "https://en.wikipedia.org/wiki/Multilayer_perceptron")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmMultilayerPerceptronPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        @Parameter(description = "Momentum applied to the weights during updating.",
            schema = @Schema(defaultValue = "0.2", example = "0.2"))@FormDataParam("momentum") Double momentum,
        @Parameter(description = "This will preprocess the instances with the filter. This could help improve performance if there are nominal attributes in the data.",
            schema = @Schema(allowableValues={"true","false"}, defaultValue = "true", example = "true")
            )@FormDataParam("nominalToBinaryFilter") Boolean nominalToBinaryFilter,
        @Parameter(description = "This defines the hidden layers of the neural network. This is a list of positive whole numbers. 1 for each hidden layer. Comma seperated. #" +
            "To have no hidden layers put a single 0 here. This will only be used if autobuild is set. There are also wildcard values 'a' = (attribs + classes) / 2, 'i' = attribs, 'o' = classes , 't' = attribs + classes.",
            schema = @Schema(defaultValue = "a", example = "a")
            )@FormDataParam("hiddenLayers") String hiddenLayers,
        @Parameter(description = "Used to terminate validation testing.The value here dictates how many times in a row the validation set error can get worse before " +
            "training is terminated.",schema = @Schema(defaultValue = "20", example = "20")
            )@FormDataParam("validationThreshold") Integer validationThreshold,

        @Parameter(description = "This will normalize the attributes. This could help improve performance of the network. This is not reliant on the class being numeric. " +
            "This will also normalize nominal attributes as well (after they have been run through the nominal to binary filter if that is in use) so that the nominal " +
            "values are between -1 and 1", schema = @Schema(allowableValues={"true","false"}, defaultValue = "true", example = "true")
            )@FormDataParam("normalizeAttributes") Boolean normalizeAttributes,
        @Parameter(description = "The number of decimal places to be used for the output of numbers in the model.")@FormDataParam("numDecimalPlaces") Integer numDecimalPlaces,
        @Parameter(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, " +
            "but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100", example = "100")
        )@FormDataParam("batchSize") Integer batchSize,
        @Parameter(description = "This will cause the learning rate to decrease. This will divide the starting learning rate by the epoch number, to determine what " +
            "the current learning rate should be. This may help to stop the network from diverging from the target output, as well as improve general performance. " +
            "Note that the decaying learning rate will not be shown in the gui, only the original learning rate. If the learning rate is changed in the gui, this is " +
            "treated as the starting learning rate.",
            schema = @Schema(allowableValues={"true","false"}, defaultValue = "true", example = "true"))@FormDataParam("decay") Boolean decay,
        @Parameter(description = "The percentage size of the validation set.(The training will continue until it is observed that the error on the validation set has been " +
            "consistently getting worse, or if the training time is reached).  If This is set to zero no validation set will be used and instead the network will train for " +
            "the specified number of epochs.", schema = @Schema(defaultValue = "0", example = "0"))@FormDataParam("validationSetSize") Integer validationSetSize,

        @Parameter(description = "The number of epochs to train through. If the validation set is non-zero then it can terminate the network early",
            schema = @Schema(defaultValue = "500", example = "500"))@FormDataParam("trainingTime") Integer trainingTime,

        @Parameter(description = "This will normalize the class if it's numeric. This could help improve performance of the network, It normalizes the class to be " +
            "between -1 and 1. Note that this is only internally, the output will be scaled back to the original range.",
            schema = @Schema(allowableValues={"true","false"}, defaultValue = "true", example = "true"))@FormDataParam("normalizeNumericClass") Boolean normalizeNumericClass,
        @Parameter(description = "The amount the weights are updated.",
            schema = @Schema(defaultValue = "0.3", example = "0.3"))@FormDataParam("learningRate") Double learningRate,

        @Parameter(description = "This will allow the network to reset with a lower learning rate. If the network diverges from the answer this will automatically " +
            "reset the network with a lower learning rate and begin training again. This option is only available if the gui is not set. " +
            "Note that if the network diverges but isn't allowed to reset it will fail the training process and return an error message.",
            schema = @Schema(allowableValues={"true","false"}, defaultValue = "true", example = "true"))@FormDataParam("reset") Boolean reset,

        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(description = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // headers
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("momentum", momentum);
        params.put("nominalToBinaryFilter", nominalToBinaryFilter);
        params.put("hiddenLayers", hiddenLayers);
        params.put("validationThreshold", validationThreshold);
        params.put("normalizeAttributes", normalizeAttributes);
        params.put("batchSize", batchSize);
        params.put("decay", decay);
        params.put("validationSetSize", validationSetSize);
        params.put("trainingTime", trainingTime);
        params.put("normalizeNumericClass", normalizeNumericClass);
        params.put("learningRate", learningRate);
        params.put("reset", reset);
        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "MultilayerPerceptron", params,
            validation, validationNum, headers, ui, securityContext);
    }


    // SMO
    @POST
    @Path("/SMO")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA SMO classifier.",
        description = "REST interface to the WEKA SMO classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/SMO")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Multilayer Perceptron", value = "https://en.wikipedia.org/wiki/Sequential_minimal_optimization")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmSMOPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        @Parameter(description = "The number of folds for cross-validation used to generate training data for calibration models (-1 means use training data).",
            schema = @Schema(minimum = "-1", defaultValue = "-1", example = "-1"))@FormDataParam("numFolds") Integer numFolds,
        @Parameter(description = "The complexity parameter C.",
            schema = @Schema(defaultValue = "1.0", example = "1.0"))@FormDataParam("c") Double c,
        @Parameter(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100", example = "100"))@FormDataParam("batchSize") Integer batchSize,
        @Parameter(description = "The kernel to use.",
            schema = @Schema(description = "The kernel to use.", defaultValue = "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007",
                example = "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007"))@FormDataParam("kernel") String kernel,
        @Parameter(description = "Determines how/if the data will be transformed. (0=normalize training data, 1=standardize training data, 2=no mormalization/standardization",
            schema = @Schema(allowableValues={"0","1","2"}, defaultValue = "0", example = "0"))@FormDataParam("filterType") Integer filterType,
        @Parameter(description = "The calibration method to use. ",
            schema = @Schema(description = "The calibration method to use.", defaultValue = "weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4",
                example = "weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4"))@FormDataParam("calibrator") String calibrator,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(description = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // headers
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("numFolds", numFolds);
        params.put("c", c);
        params.put("batchSize", batchSize);
        params.put("kernel", kernel);
        params.put("filterType", filterType);
        params.put("calibrator", calibrator);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "SMO", params,
            validation, validationNum, headers, ui, securityContext);
        }

    // SMOreg
    @POST
    @Path("/SMOreg")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA SMOreg classifier.",
        description = "REST interface to the WEKA SMOreg classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/SMOreg")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Multilayer Perceptron", value = "https://en.wikipedia.org/wiki/Sequential_minimal_optimization")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmSMOregPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        // SMOReg
        @Parameter(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100", example = "100"))@FormDataParam("batchSize") Integer batchSize,
        @Parameter(description = "The complexity parameter C.",
            schema = @Schema(defaultValue = "1.0", example = "1.0"))@FormDataParam("c") Double c,
        @Parameter(description = "Determines how/if the data will be transformed. (0=normalize training data, 1=standardize training data, 2=no mormalization/standardization",
            schema = @Schema(allowableValues={"0","1","2"}, defaultValue = "0", example = "0"))@FormDataParam("filterType") Integer filterType,
        @Parameter(description = "The kernel to use.",
            schema = @Schema(description = "The kernel to use.", defaultValue = "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007",
                example = "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007"))@FormDataParam("kernel") String kernel,
        @Parameter(description = "The calibration method to use. ",
            schema = @Schema(description = "The learning algorithm.", defaultValue = "weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4",
                example = "weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1"))@FormDataParam("regOptimizer") String regOptimizer,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(description = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // headers
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("batchSize", batchSize);
        params.put("c", c);
        params.put("filterType", filterType);
        params.put("kernel", kernel);
        params.put("regOptimizer", regOptimizer);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "SMOreg", params,
            validation, validationNum, headers, ui, securityContext);
    }

/*

  need more debuging:
  Error in ModelService.saveModel null: no.uib.cipr.matrix.UpperSPDDenseMatrix


    // Gaussian Processes
    @POST
    @Path("/GaussianProcesses")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA Gaussian Processes classifier.",
        description = "REST interface to the WEKA Gaussian Processes classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/GaussianProcesses")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Task")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Multilayer Perceptron", value = "https://en.wikipedia.org/wiki/Gaussian_process")
            })
        })
    @GroupedApiResponsesOk
    public Response algorithmGaussianProcessesPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetUri")  String datasetUri,
        // GaussianProcesses
        @Parameter(description = "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.",
            schema = @Schema(defaultValue = "100", example = "100"))@FormDataParam("batchSize") Integer batchSize,
        @Parameter(description = "Determines how/if the data will be transformed. (0=normalize training data, 1=standardize training data, 2=no mormalization/standardization",
            schema = @Schema(allowableValues={"0","1","2"}, defaultValue = "0", example = "0"))@FormDataParam("filterType") Integer filterType,
        @Parameter(description = "The kernel to use.",
            schema = @Schema(description = "The kernel to use.", defaultValue = "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007",
                example = "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007"))@FormDataParam("kernel") String kernel,
        @Parameter(description = "The level of Gaussian Noise (added to the diagonal of the Covariance Matrix), after the target has been normalized/standardized/left unchanged).",
            schema = @Schema(defaultValue = "1.0", example = "1.0"))@FormDataParam("noise") Double noise,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(allowableValues = {"CrossValidation","Hold-Out"}, defaultValue = "CrossValidation")) @FormDataParam("validation") String validation,
        @Parameter(description = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue = "10", example = "10")) @FormDataParam("validationNum") Double validationNum,
        // headers
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("batchSize", batchSize);
        params.put("filterType", filterType);
        params.put("kernel", kernel);
        params.put("noise", noise);

        return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "GaussianProcesses", params,
            validation, validationNum, headers, ui, securityContext);
    }
*/
}
