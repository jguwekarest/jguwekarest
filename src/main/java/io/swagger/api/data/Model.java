package io.swagger.api.data;


import io.swagger.api.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.Map;

@Path("/model")
//@Api(description = "Model API")

public class Model {

    @GET
    @Path("/")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/uri-list", "application/json" })
    @Operation(
        description = "List all Models",
        summary = "List all Models.",
        tags={ "model"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:void")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Models")})
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found") })
    public Response getModelList(
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

            String accept = headers.getRequestHeaders().getFirst("accept");
            Object model_list = ModelService.listModels(ui, accept, subjectid);

            return Response
                .ok(model_list)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/{id}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/plain", "application/json", "application/xml" })
    @Operation(
        description = "Get representation of a model.",
        summary = "Get representation of a model.",
        tags={ "model" },
            extensions = {
                @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model/{id}")}),
                @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
                @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
                @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Model")})
            })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))),
        //@ApiResponse( content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found") })

    public Response getModel(
        @Parameter(description = "model ID" )@PathParam("id") String id,
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui,
        @Context HttpHeaders headers) throws ApiException {

            String accept = headers.getRequestHeaders().getFirst("accept");
            Object out = ModelService.getModel(id, accept);

            return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Path("/{id}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })
    @Operation(description = "Predict testdata with a model.", summary = "Predict testdata with a model.", tags={ "model", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Prediction")})
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Resource Not Found") })
    public Response modelPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @Parameter(description = "Dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetID")  String datasetID,
        @Parameter(description = "model ID" )@PathParam("id") String id,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {
            return Response
                .ok(ModelService.predictModel(fileInputStream, fileDetail,datasetID,id,subjectid))
                .status(Response.Status.OK)
                .build();
    }


    public Map<String, Object> meta;
    public String hasSources;
    public Dataset dataset;

    public class MetaData {
        public String info;
        public String className;
        public String options;
        public Map<String, String> buildParams;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public byte[] model;

    public String validation;

}
