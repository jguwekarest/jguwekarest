package io.swagger.api.algorithm;

import io.swagger.annotations.ApiParam;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.LazyFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;

@Path("/algorithm")
@io.swagger.annotations.Api(description = "the lazy algorithm API")

public class Lazy {

    private final LazyService delegate;

    public Lazy(@Context ServletConfig servletContext) {
        LazyService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Lazy.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (LazyService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = LazyFactory.getLazy();
        }
        this.delegate = delegate;
    }


    @POST
    @Path("/kNNclassification")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "application/json"})
    @io.swagger.annotations.ApiOperation(value = "", notes = "K-nearest neighbours classifier.", response = void.class, tags={ "algorithm", })
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmKNNclassificationPost(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            , @ApiParam(value = "URI of the feature to predict", required=true)@FormDataParam("prediction_feature")  String predictionFeature
            , @ApiParam(value = "URI of the dataset to be used.", required=true)@FormDataParam("dataset_uri")  String datasetUri
            , @ApiParam(value = "URI of the data service to be used.", required=true)@FormDataParam("dataset_service")  String datasetService
            , @ApiParam(value = "Gets the maximum number of instances allowed in the training pool. The addition of new instances above this value will result in old instances being removed. A value of 0 signifies no limit to the number of training instances. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("windowSize")  Integer windowSize
            , @ApiParam(value = "The number of neighbors to use. Must be an integer greater than 0 (Default: 1).", defaultValue="1")@FormDataParam("KNN") Integer KNN
            , @ApiParam(value = "Whether hold-one-out cross-validation will be used to select the best k value. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("crossValidate")  Integer crossValidate
            , @ApiParam(value = "May be 0 for no distance weighting, I for 1/distance or F for 1-distance. Must be 0, I or F (Default: 0).", defaultValue="0")@FormDataParam("distanceWeighting")  String distanceWeighting
            , @ApiParam(value = "Whether the mean squared error is used rather than mean absolute error when doing cross-validation for regression problems. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("meanSquared")  Integer meanSquared
            , @ApiParam(value = "he nearest neighbour search algorithm to use (Default: weka.core.neighboursearch.LinearNNSearch). Fixed.", defaultValue="LinearNNSearch",readOnly = true)@FormDataParam("nearestNeighbourSearchAlgorithm")  String nearestNeighbourSearchAlgorithm
            , @ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
            , @Context SecurityContext securityContext)
            throws NotFoundException, IOException {
        return delegate.algorithmKNNclassificationPost(fileInputStream,fileDetail,predictionFeature,datasetUri,datasetService,windowSize,KNN,crossValidate,distanceWeighting,meanSquared,nearestNeighbourSearchAlgorithm,subjectid,securityContext);
    }


}
