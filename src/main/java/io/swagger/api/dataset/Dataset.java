package io.swagger.api.dataset;

import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

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

        Dataset out = DatasetService.readDataset(dataset_uri, subjectid);
        String arff = DatasetService.toArff(out, class_uri);

        //System.out.println(arff);

        return Response
                .ok(arff)
                .status(Response.Status.OK)
                .build();
    }

    public String datasetURI;
    public LinkedTreeMap meta;
    public List<Features> features;
    public List<Entries> dataEntry;

    public class Entries {
        public LinkedTreeMap compound;
        public Map<String , String> values;
    }

    public class Features {
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
