package io.swagger.api.data;


import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
@Api(description = "Model API")

public class Model {

    @GET
    @Path("/model")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/uri-list" })
    @ApiOperation(
            value = "List all Models",
            notes = "List all Models.",
            tags={ "model", },
            response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response getModelList(
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
            @ApiParam(value = "requested Content-Type" ,required=true, allowableValues="text/uri-list, application/json")@HeaderParam("Accept") String accept,
            @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

        Dao modelDao = new Dao();
        String model_list = ModelService.listModels(ui, accept, subjectid);
        System.out.println("Model list is: " + model_list);
        modelDao.close();
        return Response
                .ok(model_list)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/model/{id}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/plain" })
    @ApiOperation(
            value = "Get representation of a model.",
            notes = "Get representation of a model.",
            tags={ "model", },
            response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response getModel(
            @ApiParam(value = "model ID" )@PathParam("id") String id,
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui) throws ApiException {

        String out = ModelService.getModel(id);

        return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }


    public LinkedTreeMap meta;
    public String datasetURI;
    public Dataset dataset;

    public byte[] model;
    public String info;
    public String validation;

}
