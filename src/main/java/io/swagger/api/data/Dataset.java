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
    @Produces({ "text/x-arff" })
    @ApiOperation(
            value = "Download dataset and convert into weka arff format",
            notes = "Download an external dataset and convert it into weka arff format.",
            tags={ "dataset", },
            response = void.class,
            produces = "text/x-arff")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response create(
              @ApiParam(value = "URI of the dataset to be used.", required=true)@FormDataParam("dataset_uri") String dataset_uri
            , @ApiParam(value = "URI of the feature to define as weka class", required=false)@FormDataParam("class_uri") String class_uri
            , @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid) throws ApiException {

        Dataset ds = DatasetService.readExternalDataset(dataset_uri, subjectid);
        if (ds.datasetURI == null) ds.datasetURI = dataset_uri;
        String arff = DatasetService.toArff(ds, class_uri);

        //System.out.println(arff);

        return Response
                .ok(arff)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/dataset")
    @Produces({ "text/uri-list", "application/json" })
    @ApiOperation(
            value = "List all converted datasets.",
            notes = "List all converted datasets.",
            tags={ "dataset", },
            response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response list(
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
            @ApiParam(value = "requested Content-Type" ,required=true, allowableValues="text/uri-list, application/json")@HeaderParam("Accept") String accept,
            @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

        String datasetList = DatasetService.listDatasets(ui, accept, subjectid);

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
            tags={ "dataset", },
            response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response getDatasetArff(
            @ApiParam(value = "Dataset ID" )@PathParam("id") String id,
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui) throws ApiException, NotFoundException {

        String out = DatasetService.getDatasetArff(id, subjectid);

        return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }

    public String datasetURI;
    public LinkedTreeMap meta;
    public List<Feature> features;
    public List<DataEntry> dataEntry;

    public String arffFileName;
    public String arff;

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
