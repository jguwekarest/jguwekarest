package io.swagger.api.algorithm;


import io.swagger.annotations.*;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.FunctionsFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@Path("/algorithm")
@Api(description = "the lazy algorithm API")

public class Functions {

    private final FunctionsService delegate;

    public Functions(@Context ServletConfig servletContext) {
        FunctionsService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Functions.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (FunctionsService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = FunctionsFactory.getFunctions();
        }
        this.delegate = delegate;
    }


    @POST
    @Path("/linearRegression")
    @Consumes({"multipart/form-data"})
    @Produces({"text/x-arff", "application/json"})
    @ApiOperation(value = "", notes = "K-nearest neighbours classifier.", response = void.class, tags = {"algorithm",}, position=3)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class)})
    public Response algorithmKNNclassificationPost(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            , @ApiParam(value = "URI of the feature to predict", required = true) @FormDataParam("prediction_feature") String predictionFeature
            , @ApiParam(value = "URI of the dataset to be used.", required = true) @FormDataParam("dataset_uri") String datasetUri
            , @ApiParam(value = "URI of the data service to be used.", required = true) @FormDataParam("dataset_service") String datasetService
            , @ApiParam(value = "Attribute selection method to be used (Default M5 method).Available methods are: no attribute selection(Value:1), attribute selection using M5's method (Value:0) and a greedy selection using the Akaike information metric(Value:2). One of 0,1,2 (Default: 0).", defaultValue = "0", allowableValues = "0, 1, 2" ) @FormDataParam("attributeSelectionMethod") Integer attributeSelectionMethod
            , @ApiParam(value = "Whether to eliminate colinear attributes. Must be 0 or 1 (Default: 1).", defaultValue = "1", allowableValues = "0, 1") @FormDataParam("eliminateColinearAttributes") Integer eliminateColinearAttributes
            , @ApiParam(value = "The ridge parameter (Default: 1.0E-8).", defaultValue = "1.0E-8") @FormDataParam("ridge") BigDecimal ridge
            , @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid
            , @Context SecurityContext securityContext)
            throws NotFoundException, IOException {
        return delegate.linearRegressionPost(fileInputStream, fileDetail, predictionFeature, datasetUri, datasetService, attributeSelectionMethod, eliminateColinearAttributes, ridge, subjectid, securityContext);
    }
}
