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
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10")) @FormDataParam("validationNum") Double validationNum,
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
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10")) @FormDataParam("validationNum") Double validationNum,
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
        @Parameter(description = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue = "10")) @FormDataParam("validationNum") Double validationNum,
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

    /*momentum -- Momentum applied to the weights during updating.
    nominalToBinaryFilter -- This will preprocess the instances with the filter. This could help improve performance if there are nominal attributes in the data.
    hiddenLayers -- This defines the hidden layers of the neural network. This is a list of positive whole numbers. 1 for each hidden layer. Comma seperated. To have no hidden layers put a single 0 here. This will only be used if autobuild is set. There are also wildcard values 'a' = (attribs + classes) / 2, 'i' = attribs, 'o' = classes , 't' = attribs + classes.
    validationThreshold -- Used to terminate validation testing.The value here dictates how many times in a row the validation set error can get worse before training is terminated.
    GUI -- Brings up a gui interface. This will allow the pausing and altering of the nueral network during training.
        * To add a node left click (this node will be automatically selected, ensure no other nodes were selected).
        * To select a node left click on it either while no other node is selected or while holding down the control key (this toggles that node as being selected and not selected.
        * To connect a node, first have the start node(s) selected, then click either the end node or on an empty space (this will create a new node that is connected with the selected nodes). The selection status of nodes will stay the same after the connection. (Note these are directed connections, also a connection between two nodes will not be established more than once and certain connections that are deemed to be invalid will not be made).
        * To remove a connection select one of the connected node(s) in the connection and then right click the other node (it does not matter whether the node is the start or end the connection will be removed).
        * To remove a node right click it while no other nodes (including it) are selected. (This will also remove all connections to it)
        .* To deselect a node either left click it while holding down control, or right click on empty space.
        * The raw inputs are provided from the labels on the left.
        * The red nodes are hidden layers.
        * The orange nodes are the output nodes.
        * The labels on the right show the class the output node represents. Note that with a numeric class the output node will automatically be made into an unthresholded linear unit.

        Alterations to the neural network can only be done while the network is not running, This also applies to the learning rate and other fields on the control panel.
        * You can accept the network as being finished at any time.
        * The network is automatically paused at the beginning.
        * There is a running indication of what epoch the network is up to and what the (rough) error for that epoch was (or for the validation if that is being used). Note that this error value is based on a network that changes as the value is computed. (also depending on whether the class is normalized will effect the error reported for numeric classes.
        * Once the network is done it will pause again and either wait to be accepted or trained more.

       Note that if the gui is not set the network will not require any interaction.


    normalizeAttributes -- This will normalize the attributes. This could help improve performance of the network. This is not reliant on the class being numeric. This will also normalize nominal attributes as well (after they have been run through the nominal to binary filter if that is in use) so that the nominal values are between -1 and 1
    numDecimalPlaces -- The number of decimal places to be used for the output of numbers in the model.
    batchSize -- The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.
    decay -- This will cause the learning rate to decrease. This will divide the starting learning rate by the epoch number, to determine what the current learning rate should be. This may help to stop the network from diverging from the target output, as well as improve general performance. Note that the decaying learning rate will not be shown in the gui, only the original learning rate. If the learning rate is changed in the gui, this is treated as the starting learning rate.
    validationSetSize -- The percentage size of the validation set.(The training will continue until it is observed that the error on the validation set has been consistently getting worse, or if the training time is reached).
        If This is set to zero no validation set will be used and instead the network will train for the specified number of epochs.

    trainingTime -- The number of epochs to train through. If the validation set is non-zero then it can terminate the network early
    debug -- If set to true, classifier may output additional info to the console.
    autoBuild -- Adds and connects up hidden layers in the network.
    normalizeNumericClass -- This will normalize the class if it's numeric. This could help improve performance of the network, It normalizes the class to be between -1 and 1. Note that this is only internally, the output will be scaled back to the original range.
    learningRate -- The amount the weights are updated.
    doNotCheckCapabilities -- If set, classifier capabilities are not checked before classifier is built (Use with caution to reduce runtime).
    reset -- This will allow the network to reset with a lower learning rate. If the network diverges from the answer this will automatically reset the network with a lower learning rate and begin training again. This option is only available if the gui is not set. Note that if the network diverges but isn't allowed to reset it will fail the training process and return an error message.
*/


}
