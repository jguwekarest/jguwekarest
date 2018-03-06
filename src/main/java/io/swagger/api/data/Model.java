package io.swagger.api.data;


import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.Map;

@Path("/model")
@Api(description = "Model API")

public class Model {

    @GET
    @Path("/")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/uri-list", "application/json" })
    @ApiOperation(
        value = "List all Models",
        notes = "List all Models.",
        tags={ "model"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:void")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Models")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response getModelList(
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
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
    @Produces({ "text/plain", "application/json" })
    @ApiOperation(
        value = "Get representation of a model.",
        notes = "Get representation of a model.",
        tags={ "model" },
            extensions = {
                @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model/{id}")}),
                @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
                @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
                @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Model")})
            })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response getModel(
        @ApiParam(value = "model ID" )@PathParam("id") String id,
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
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
    @ApiOperation(value = "Predict testdata with a model.", notes = "Predict testdata with a model.", tags={ "model", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Prediction")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response modelPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetID")  String datasetID,
        @ApiParam(value = "model ID" )@PathParam("id") String id,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
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
