package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.LazyFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;

@Path("/algorithm")
@Api(description = "the lazy algorithm API")

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
    @Produces({ "text/x-arff", "text/uri-list"})
    @ApiOperation(
        value = "REST interface to the WEKA K-nearest neighbours classifier.",
        notes = "REST interface to the WEKA K-nearest neighbours classifier. " + SAVE_MODEL_NOTE,
        tags={ "algorithm", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/kNNclassification")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "k-nearest neighbors algorithm", value = "https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response algorithmKNNclassificationPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "Gets the maximum number of instances allowed in the training pool. The addition of new instances above this value will result in old instances being removed. A value of 0 signifies no limit to the number of training instances. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("windowSize")  Integer windowSize,
        @ApiParam(value = "The number of neighbors to use. Must be an integer greater than 0 (Default: 1).", defaultValue="1")@FormDataParam("KNN") Integer KNN,
        @ApiParam(value = "Whether hold-one-out cross-validation will be used to select the best k value. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("crossValidate")  Integer crossValidate,
        @ApiParam(value = "May be 0 for no distance weighting, I for 1/distance or F for 1-distance. Must be 0, I or F (Default: 0).", defaultValue="0")@FormDataParam("distanceWeighting")  String distanceWeighting,
        @ApiParam(value = "Whether the mean squared error is used rather than mean absolute error when doing cross-validation for regression problems. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("meanSquared")  Integer meanSquared,
        @ApiParam(value = "The nearest neighbour search algorithm to use (Default: weka.core.neighboursearch.LinearNNSearch). Fixed.", defaultValue="LinearNNSearch")@FormDataParam("nearestNeighbourSearchAlgorithm")  String nearestNeighbourSearchAlgorithm,
        @ApiParam(value = "Authorization token" ) @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers)
        throws NotFoundException, IOException {
        return delegate.algorithmKNNclassificationPost(fileInputStream,fileDetail,datasetUri,windowSize,KNN,crossValidate,distanceWeighting,meanSquared,
            nearestNeighbourSearchAlgorithm,subjectid,headers,ui);
    }

}
