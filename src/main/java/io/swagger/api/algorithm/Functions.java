package io.swagger.api.algorithm;


import io.swagger.annotations.*;
import io.swagger.api.AlgorithmService;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.AlgorithmFactory;
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
@Api(description = "the functions algorithm API")

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
    @ApiOperation(value = "REST interface to the WEKA linear regression classifier.",
        notes = "REST interface to the WEKA linear regression classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/linearRegression")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Linear Regression", value = "https://en.wikipedia.org/wiki/Linear_regression")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"/*, response = ApiException.class*/),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found")})
    public Response algorithmLRPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "Attribute selection method to be used (Default M5 method).Available methods are: no attribute selection(Value:1), attribute selection using M5's method (Value:0) and a greedy selection using the Akaike information metric(Value:2). One of 0,1,2 (Default: 0).", defaultValue = "0", allowableValues = "0, 1, 2" ) @FormDataParam("attributeSelectionMethod") Integer attributeSelectionMethod,
        @ApiParam(value = "Whether to eliminate colinear attributes. Must be 0 or 1 (Default: 1).", defaultValue = "1", allowableValues = "0, 1") @FormDataParam("eliminateColinearAttributes") Integer eliminateColinearAttributes,
        @ApiParam(value = "The ridge parameter (Default: 1.0E-8).", defaultValue = "1.0E-8") @FormDataParam("ridge") BigDecimal ridge,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {

            HashMap<String, Object> params = new HashMap<>();
            params.put("datasetUri", datasetUri);
            params.put("attributeSelectionMethod", attributeSelectionMethod);
            params.put("eliminateColinearAttributes", eliminateColinearAttributes);
            params.put("ridge", ridge);
    //attributeSelectionMethod, eliminateColinearAttributes, ridge
            return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "LinearRegression", params,
                                          headers, ui, securityContext);
    }


    @POST
    @Path("/libsvm")
    @Consumes({"multipart/form-data"})
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "REST interface to the WEKA support vector machine wrapper library classifier.",
        notes = "REST interface to the WEKA support vector machine wrapper library classifier." + SAVE_MODEL_NOTE,
        tags = {"algorithm"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/libsvm")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "support vector machine", value = "https://en.wikipedia.org/wiki/Support_vector_machine")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found")})
    public Response algorithmLibSVMPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "SVMType -- The type of SVM to use. \n 0: C-SVC (classification) \n 1: nu-SVC (classification) \n 2: one-class SVM (classification) \n 3: epsilon-SVR (regression)\n 4: nu-SVR (regression)\n (Default: 0).", defaultValue = "0", allowableValues = "0,1,2,3,4" ) @FormDataParam("svmType") Integer svmType,
        @ApiParam(value = "coef0 -- The coefficient to use. (Default: 0).", defaultValue = "0") @FormDataParam("coef0") Float coef0,
        @ApiParam(value = "cost -- The cost parameter C for C-SVC, epsilon-SVR and nu-SVR. (Default: 1.0).", defaultValue = "1.0") @FormDataParam("cost") Float cost,
        @ApiParam(value = "degree -- The degree of the kernel. (Default: 3).", defaultValue = "3") @FormDataParam("degree") Integer degree,
        @ApiParam(value = "eps -- The tolerance of the termination criterion. (Default: 0.001).", defaultValue = "0.001") @FormDataParam("eps") BigDecimal eps,
        @ApiParam(value = "gamma -- The gamma to use, if 0 then 1/max_index is used. (Default: 0.0).", defaultValue = "0.0") @FormDataParam("gamma") BigDecimal gamma,
        @ApiParam(value = "kernelType -- The type of kernel to use.\n 0: linear:u'*v \n 1: polynomial: (gamma*u'*v + coef0)^degree \n 2: radial basis function: exp(-gamma*|u-v|^2) \n 3: sigmoid: tanh()gamma*u'*v + coef0) \n (Default: 2).", defaultValue = "2", allowableValues = "0,1,2,3" ) @FormDataParam("kernelType") Integer kernelType,
        @ApiParam(value = "loss -- The epsilon for the loss function in epsilon-SVR. (Default: 0.1).", defaultValue = "0.1") @FormDataParam("loss") BigDecimal loss,
        @ApiParam(value = "normalize -- Whether to normalize the data.", defaultValue = "false") @FormDataParam("normalize") Boolean normalize,
        @ApiParam(value = "nu -- The value of nu for nu-SVC, one-class SVM and nu-SVR. (Default: 0.5).", defaultValue = "0.5") @FormDataParam("nu") BigDecimal nu,
        @ApiParam(value = "probabilityEstimates -- Whether to generate probability estimates instead of -1/+1 for classification problems.", defaultValue = "false") @FormDataParam("probabilityEstimates") Boolean probabilityEstimates,
        @ApiParam(value = "shrinking -- Whether to use the shrinking heuristic.", defaultValue = "true") @FormDataParam("shrinking") Boolean shrinking,
        @ApiParam(value = "weights -- The weights to use for the classes (blank-separated list, eg, \"1 1 1\" for a 3-class problem), if empty 1 is used by default.") @FormDataParam("weights") String weights,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
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
                                          headers, ui, securityContext);
    }
}
