package io.swagger.api.dataset;

import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/dataset")
@Api(description = "Dataset API")

public class Dataset {

    @POST
    @Path("/")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })
    @ApiOperation(
            value = "Download dataset and convert into weka arff format",
            notes = "Download an external dataset and convert it into weka arff format.",
            tags={ "dataset", },
            response = void.class,
            produces = "text/x-arff")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Wrong, missing or insufficient credentials. Error report is produced."),
            @ApiResponse(code = 200, message = "Logged in - authentication token can be found in the response body (in JSON)")
    })
    public Response create(
              @ApiParam(value = "URI of the dataset to be used.", required=true)@FormDataParam("dataset_uri") String dataset_uri
            , @ApiParam(value = "URI of the feature for weka class", required=true)@FormDataParam("feature_uri") String predictionFeature
            , @ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid) throws ApiException {

        Dataset out = DatasetService.readDataset(dataset_uri, subjectid);
        String arff = DatasetService.toArff(out);

        System.out.println(arff);

        return Response
                .ok("Dataset Create Response:\n" + arff)
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
        public String category;
        public String uri;
    }

    public class MetaData {
        public String comments;
        public String descriptions;
        public String titles;
    }
}
