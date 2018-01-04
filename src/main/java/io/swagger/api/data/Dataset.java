package io.swagger.api.data;

import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.TreeMap;

@Path("/")
@Api(description = "Dataset API")

public class Dataset {

    @POST
    @Path("/dataset")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "text/uri-list" })
    @ApiOperation(
            value = "Download dataset and convert into weka arff format",
            notes = "Download an external dataset and convert it into weka arff format.",
            tags={ "dataset", },
            produces = "text/x-arff" )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response create(
              @ApiParam(value = "URI of the dataset to be used.", required=true)@FormDataParam("dataset_uri") String dataset_uri
            , @ApiParam(value = "URI of the feature to define as weka class")@FormDataParam("class_uri") String class_uri
            , @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid) throws ApiException {

        Dataset ds = DatasetService.readExternalDataset(dataset_uri, subjectid);
        if (ds.datasetURI == null) ds.datasetURI = dataset_uri;
        String out = DatasetService.toArff(ds, class_uri);

        return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/dataset")
    @Produces({ "text/uri-list", "application/json" })
    @ApiOperation(
            value = "List all converted datasets.",
            notes = "List all converted datasets.",
            tags={ "dataset", })
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
        System.out.println("D accept: " + accept);
        Object datasetList = DatasetService.listDatasets(ui, accept, subjectid);

        return Response
                .ok(datasetList)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/dataset/{id}/arff")
    @Produces({ "text/x-arff" })
    @ApiOperation(
            value = "Get arff representation of a dataset.",
            notes = "Get arff representation of a dataset.",
            tags={ "dataset", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response getDatasetArff(
            @ApiParam(value = "Dataset ID" )@PathParam("id") String id,
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui) throws ApiException, NotFoundException {

        String out = DatasetService.getDatasetArff(id, subjectid);

        return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }


    @POST
    @Path("/dataset/{id}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })
    @ApiOperation(
            value = "Filter an internal dataset with weka filter.",
            notes = "Filter an internal dataset with weka filter. Remove attributes and normalize or standardize all numeric attributes of a dataset.",
            tags={ "dataset" },
            produces = "text/x-arff")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response filter(
            @ApiParam(value = "Dataset ID" )@PathParam("id") String id
            , @ApiParam(value = "Feature(s) to remove. ID or comma separated IDs of the attribute(s)(column(s)) to remove. (0 = no attribute will be deleted)",defaultValue = "0")@FormDataParam("idx_remove") String idx_remove
            , @ApiParam(value = "Normalize all numeric values - scale: - The factor for scaling the output range (default: 1).")@FormDataParam("scale") String scale
            , @ApiParam(value = "Normalize all numeric values - translation: The translation of the output range (default: 0).")@FormDataParam("translation") String translation
            , @ApiParam(value = "Standardize all numeric attributes in the given dataset to have zero mean and unit variance (apart from the class attribute, if set).")@FormDataParam("standardize") Boolean standardize
            , @ApiParam(value = "Ignore class (ignore class attribute for Normalization or Standization).")@FormDataParam("ignore") Boolean ignore
            , @ApiParam(value = "String to Nominal: Sets which attributes to process. This attributes must be string attributes (\"first\" and \"last\" are valid values as well as ranges and lists. Empty value do not process the filter).")@FormDataParam("attributeRange") String attributeRange
            , @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid) throws Exception {

        String arff = DatasetService.getArff(null,null, id, subjectid);
        Dataset ds = new Dataset();
        ds.datasetURI = id; // @ToDo replace with full URI
        ds.arff = arff;
        String newArff = DatasetService.filter(ds, idx_remove, scale, translation, standardize, ignore, attributeRange);

        return Response
                .ok(newArff)
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
