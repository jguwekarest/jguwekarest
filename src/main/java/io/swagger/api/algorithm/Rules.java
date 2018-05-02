package io.swagger.api.algorithm;

import io.swagger.api.AlgorithmService;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.AlgorithmFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;
import static io.swagger.api.Constants.TEXT_URILIST;

@Path("/algorithm")
//@Api(description = "Rules algorithm API")

public class Rules {

    private final AlgorithmService delegate;

    public Rules(@Context ServletConfig servletContext) {
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
    @Path("/ZeroR")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA ZeroR classifier.",
        description = "REST interface to the WEKA ZeroR classifier. " + SAVE_MODEL_NOTE,
        tags={ "algorithm" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/ZeroR")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "ZeroR", value = "http://weka.sourceforge.net/doc.dev/weka/classifiers/rules/ZeroR.html")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found") })
    public Response algorithmZeroRclassificationPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo uriInfo, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws NotFoundException, IOException {
            HashMap<String, Object> params = new HashMap<>();

            return delegate.algorithmPost(fileInputStream,fileDetail,datasetUri,"ZeroR", params,
                                          validation, validationNum, headers, uriInfo, securityContext);
    }


    @POST
    @Path("/M5Rules")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "REST interface to the WEKA M5Rules classifier.",
        description = "REST interface to the WEKA M5Rules classifier. " + SAVE_MODEL_NOTE,
        tags={ "algorithm" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/M5Rules")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = { @ExtensionProperty(name = "M5Rules", value = "http://weka.sourceforge.net/doc.dev/weka/classifiers/rules/M5Rules.html")})
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found") })
    public Response algorithmM5RclassificationPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @Parameter(
            description = "Whether pruning is performed.", example = "0",
            schema = @Schema(defaultValue = "0", allowableValues="0,1"))@FormDataParam("unpruned") Integer unpruned,
        @Parameter(
            description = "Whether to use unsmoothed predictions.",
            schema = @Schema(defaultValue = "0", allowableValues="0,1"))@FormDataParam("useUnsmoothed") Integer useUnsmoothed,
        @Parameter(
            description = "The minimum number of instances to allow at a leaf node.",
            schema = @Schema(defaultValue = "4"))@FormDataParam("minNumInstances") Double minNumInstances,
        @Parameter(
            description = "Whether to generate a regression tree/rule instead of a model tree/rule.",
            schema = @Schema(defaultValue = "0", allowableValues="0,1"))@FormDataParam("buildRegressionTree") Integer buildRegressionTree,
        // validation
        @Parameter(description = "Validation to use.", schema = @Schema(defaultValue="CrossValidation", allowableValues = {"CrossValidation", "Hold-Out"})) @FormDataParam("validation") String validation ,
        @Parameter(description  = "Num of Crossvalidations or Percentage Split %.", schema = @Schema(defaultValue="10")) @FormDataParam("validationNum") Double validationNum,
        // authorization
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo uriInfo, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {
            HashMap<String, Object> params = new HashMap<>();
            params.put("datasetUri", datasetUri);
            params.put("unpruned", unpruned);
            params.put("useUnsmoothed", useUnsmoothed);
            params.put("minNumInstances", minNumInstances);
            params.put("buildRegressionTree", buildRegressionTree);

            return delegate.algorithmPost(fileInputStream, fileDetail, datasetUri, "M5Rules", params,
                                          validation, validationNum, headers, uriInfo, securityContext);
    }

}
