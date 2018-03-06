package io.swagger.api.data;

import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.TreeMap;

import static io.swagger.api.Constants.SAVE_DATASSET_NOTE;
import static io.swagger.api.Constants.TEXT_ARFF;
import static io.swagger.api.Constants.TEXT_URILIST;

@Path("/")
@Api(description = "Dataset API")

public class Dataset {

    @POST
    @Path("/dataset")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_ARFF, TEXT_URILIST })
    @ApiOperation(
        value = "Download dataset and convert into weka arff format.",
        notes = "Download an external dataset and convert it into weka arff format. " + SAVE_DATASSET_NOTE,
        tags={ "dataset", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Dataset")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:URI")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response create(
        @ApiParam(value = "URI of the dataset to be used.", required=true)@FormDataParam("dataset_uri") String dataset_uri,
        @ApiParam(value = "URI of the feature to define as weka class")@FormDataParam("class_uri") String class_uri,
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context HttpHeaders headers, @Context UriInfo ui) throws ApiException {

            Dataset ds = DatasetService.readExternalDataset(dataset_uri, subjectid);
            if (ds.datasetURI == null) ds.datasetURI = dataset_uri;
            String accept = headers.getRequestHeaders().getFirst("accept");

            String out = DatasetService.toArff(ds, class_uri, accept, ui);

            return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/dataset")
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @ApiOperation(
        value = "List all converted datasets.",
        notes = "List all converted datasets.",
        tags={ "dataset", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:URIList")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:URIList")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response list(
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

            String accept = headers.getRequestHeaders().getFirst("accept");
            Object datasetList = DatasetService.listDatasets(ui, accept, subjectid);

            return Response
                .ok(datasetList)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/dataset/{id}/arff")
    @Produces({ TEXT_ARFF })
    @ApiOperation(
        value = "Get arff representation of a dataset.",
        notes = "Get arff representation of a dataset.",
        tags={ "dataset", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset/{id}/arff")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Dataset")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Arff")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response getDatasetArff(
        @ApiParam(value = "Dataset ID" )@PathParam("id") String id,
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui)
        throws ApiException, NotFoundException {

            String out = DatasetService.getDatasetArff(id, subjectid);

            return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }


    @POST
    @Path("/dataset/{id}")
    @Consumes({ "multipart/form-data" })
    @Produces({TEXT_ARFF, TEXT_URILIST})
    @ApiOperation(
        value = "Filter an internal dataset with weka filter.",
        notes = "Filter an internal dataset with weka filter. Remove attributes and normalize or standardize all numeric attributes of a dataset." + SAVE_DATASSET_NOTE,
        tags={ "dataset" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Dataset")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response filter(
        @ApiParam(value = "Dataset ID" )@PathParam("id") String id,
        @ApiParam(value = "Feature(s) to remove. ID or comma separated IDs of the attribute(s)(column(s)) to remove. (0 = no attribute will be deleted)",defaultValue = "0")@FormDataParam("idx_remove") String idx_remove,
        @ApiParam(value = "Normalize all numeric values - scale: - The factor for scaling the output range (default: 1).")@FormDataParam("scale") String scale,
        @ApiParam(value = "Normalize all numeric values - translation: The translation of the output range (default: 0).")@FormDataParam("translation") String translation,
        @ApiParam(value = "Standardize all numeric attributes in the given dataset to have zero mean and unit variance (apart from the class attribute, if set).")@FormDataParam("standardize") Boolean standardize,
        @ApiParam(value = "Ignore class (ignore class attribute for Normalization or Standization).")@FormDataParam("ignore") Boolean ignore,
        @ApiParam(value = "String to Nominal: Sets which attributes to process. This attributes must be string attributes (\"first\" and \"last\" are valid values as well as ranges and lists. Empty value do not process the filter).")@FormDataParam("attributeRange") String attributeRange,
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context HttpHeaders headers, @Context UriInfo ui ) throws Exception{

        String accept = headers.getRequestHeaders().getFirst("accept");
        Dataset ds = DatasetService.getDataset(id, subjectid);
        String uri = ui.getBaseUri().toString();

        String output = DatasetService.filter(ds, idx_remove, scale, translation, standardize, ignore, attributeRange, accept, uri);

        return Response
                .ok(output)
                .status(Response.Status.OK)
                .build();
    }


    //dataset structure
    public String URI;
    public String datasetURI;
    public LinkedTreeMap meta;
    public List<Feature> features;
    public List<DataEntry> dataEntry;

    public String arffFileName;
    public String arff;
    public String comment;

    public class DataEntry {
        public LinkedTreeMap compound;
        public TreeMap<String, String> values;
    }

    public class Feature {
        public String name;
        public String units;
        public LinkedTreeMap conditions;
        public String category;
        public String uri;
    }

    public class MetaData {
        public String comments;
        public String descriptions;
        public String titles;
    }

}
