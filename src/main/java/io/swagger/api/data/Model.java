package io.swagger.api.data;


import io.swagger.api.ApiException;
import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.Map;

@Path("/model")
//@Api(description = "Model API")
@Schema(name = "model", description = "model")
public class Model {

    @GET
    @Path("/")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/uri-list", "application/json" })
    @Operation(
        summary = "List all Models",
        description = "List all Models.",
        tags={ "model"},
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:void")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ModelList")})
        })
    @GroupedApiResponsesOk
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
    @Produces({ "text/plain", "application/json"})
    @Operation(
        summary = "Get representation of a model.",
        description = "Get representation of a model.",
        tags={ "model" },
            extensions = {
                @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model/{id}")}),
                @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
                @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ModelId")}),
                @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Model")})
            })
    @GroupedApiResponsesOk
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
    @Operation(
        summary = "Predict testdata with a model.",
        description = "Predict testdata with a model.", tags={ "model", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/model/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Model")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ModelId")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Prediction")})
        })
    @GroupedApiResponsesOk
    public Response modelPost(
        @Parameter(schema = @Schema(description="ARFF data file.", type = "string", format = "binary")) @FormDataParam("file") InputStream fileInputStream,
        @Parameter(description = "Dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetID")  String datasetID,
        @Parameter(description = "model ID" )@PathParam("id") String id,
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws Exception {
            return Response
                .ok(ModelService.predictModel(fileInputStream, datasetID,id,subjectid))
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
