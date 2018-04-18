package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.AlgorithmService;
import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.api.factories.AlgorithmFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;
import static io.swagger.api.Constants.TEXT_URILIST;

@Path("/algorithm")
@Api(description = "the functions algorithm API")

public class Generic {

    private final AlgorithmService delegate;

    public Generic(@Context ServletConfig servletContext) {
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
     * generic REST interface to WEKA algorithms
     */
    @POST
    @Path("/generic")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Generic REST interface to WEKA classifiers.", notes = "Generic REST interface to WEKA classifiers. Select a classifier and add parameter string as shown in WEKA Explorer. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/generic")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),}
    )
    @GroupedApiResponsesOk

    public Response algorithmGenericPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "Classifier to use.", allowableValues = "BayesNet,DecisionStump,GaussianProcesses,J48,IBk,LibSVM,LinearRegression,Logistic,M5P,M5Rules,MultilayerPerceptron,NaiveBayes,RandomForest,SMO,SMOreg,ZeroR,AdaBoost,Bagging") @FormDataParam("classifierString") String classifierString,
        @ApiParam(value = "Parameter String. As shown as in WEKA Explorer classifierer line", example = "for SMO: -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"")@FormDataParam("paramString") String paramString,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        return delegate.algorithmGenericPost(fileInputStream, fileDetail, datasetUri, classifierString, paramString,
            headers, ui, securityContext);
    }

    /**
     * generic REST interface to WEKA algorithms get options description
     */
    @GET
    @Path("/generic")
    @Consumes({ "multipart/form-data" })
    @Produces({ MediaType.TEXT_PLAIN})
    @ApiOperation(value = "Generic REST interface to WEKA classifiers.", notes = "Generic REST interface to WEKA classifiers. Select a classifier and list options for the classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/generic")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),}
    )
    @GroupedApiResponsesOk

    public Response algorithmGenericGet(
        @ApiParam(value = "List Options for classifier.", allowableValues = "BayesNet,DecisionStump,GaussianProcesses,J48,IBk,LibSVM,LinearRegression,Logistic,M5P,M5Rules,MultilayerPerceptron,NaiveBayes,RandomForest,SMO,SMOreg,ZeroR,AdaBoost,Bagging") @HeaderParam("classifierName") String classifierString,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {

        return delegate.algorithmGenericGet(classifierString, headers, ui, securityContext);
    }

}
