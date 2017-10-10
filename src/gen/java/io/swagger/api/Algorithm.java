package io.swagger.api;

import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.api.factories.AlgorithmFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

@Path("/algorithm")


@io.swagger.annotations.Api(description = "the algorithm API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class Algorithm  {
   private final AlgorithmService delegate;

   public Algorithm(@Context ServletConfig servletContext) {
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


    @GET
    @Path("/{algorithmname}")

    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "Get algorithm representation", response = void.class, tags={ "algorithm", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmAlgorithmnameGet(@ApiParam(value = "requested Content-Type" ,required=true, allowableValues="application/json")@HeaderParam("Accept") String accept
        ,@ApiParam(value = "name of an algorithm",required=true) @PathParam("algorithmname") String algorithmname
        ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
        ,@Context SecurityContext securityContext, @Context ServletContext servletContext)
        throws NotFoundException, IOException {
            return delegate.algorithmAlgorithmnameGet(accept,algorithmname,subjectid,securityContext,servletContext);
    }
    @POST
    @Path("/{algorithmname}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "Train data on algorithm", response = void.class, tags={ "algorithm", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmAlgorithmnamePost(@ApiParam(value = "SMILES identifier or Nanoparticle URI or comma separated list of SMILES  identifiers or Nanoparticle URI", required=true)@FormDataParam("identifier")  String identifier
        ,@ApiParam(value = "name of an algorithm",required=true) @PathParam("algorithmname") String algorithmname
        ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
        ,@Context SecurityContext securityContext)
        throws NotFoundException {
            return delegate.algorithmAlgorithmnamePost(identifier,algorithmname,subjectid,securityContext);
    }
    @GET
    
    
    @Produces({ "text/uri-list", "application/json" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "Get a list of all algorithms", response = void.class, tags={ "algorithm", },extensions = @Extension(name = "my-extension", properties = { @ExtensionProperty(name = "test1", value = "value1")}))
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Resource Not Found", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error", response = void.class) })
    public Response algorithmGet(@ApiParam(value = "requested Content-Type" ,required=true, allowableValues="text/uri-list, application/json")@HeaderParam("Accept") String accept
    ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
    ,@Context SecurityContext securityContext)
        throws NotFoundException {
            return delegate.algorithmGet(accept,subjectid,securityContext);
    }

    @POST
    @Path("/kNNclassification")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "K-nearest neighbours classifier.", response = void.class, tags={ "algorithm", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden", response = void.class),
        @io.swagger.annotations.ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmKNNclassificationPost(@ApiParam(value = "URI of the feature to predict", required=true)@FormDataParam("prediction_feature")  String predictionFeature
        ,@ApiParam(value = "URI of the dataset to be used.", required=true)@FormDataParam("dataset_uri")  String datasetUri
        ,@ApiParam(value = "URI of the data service to be used.", required=true)@FormDataParam("dataset_service")  String datasetService
        ,@ApiParam(value = "Gets the maximum number of instances allowed in the training pool. The addition of new instances above this value will result in old instances being removed. A value of 0 signifies no limit to the number of training instances. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("windowSize")  Integer windowSize
        ,@ApiParam(value = "The number of neighbors to use. Must be an integer greater than 0 (Default: 1).", defaultValue="1")@FormDataParam("KNN")  Integer KNN
        ,@ApiParam(value = "Whether hold-one-out cross-validation will be used to select the best k value. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("crossValidate")  Integer crossValidate
        ,@ApiParam(value = "May be 0 for no distance weighting, I for 1/distance or F for 1-distance. Must be 0, I or F (Default: 0).", defaultValue="0")@FormDataParam("distanceWeighting")  Integer distanceWeighting
        ,@ApiParam(value = "Whether the mean squared error is used rather than mean absolute error when doing cross-validation for regression problems. Must be 0 or 1 (Default: 0).", defaultValue="0")@FormDataParam("meanSquared")  Integer meanSquared
        ,@ApiParam(value = "he nearest neighbour search algorithm to use (Default: weka.core.neighboursearch.LinearNNSearch). Fixed.", defaultValue="LinearNNSearch")@FormDataParam("nearestNeighbourSearchAlgorithm")  String nearestNeighbourSearchAlgorithm
        ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
        ,@Context SecurityContext securityContext)
        throws NotFoundException {
            return delegate.algorithmKNNclassificationPost(predictionFeature,datasetUri,datasetService,windowSize,KNN,crossValidate,distanceWeighting,meanSquared,nearestNeighbourSearchAlgorithm,subjectid,securityContext);
    }
}
