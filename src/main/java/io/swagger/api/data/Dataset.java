package io.swagger.api.data;

import com.google.gson.internal.LinkedTreeMap;
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
import java.util.List;
import java.util.TreeMap;

import static io.swagger.api.Constants.*;

@Path("/")
//@Api(description = "Dataset API")

public class Dataset {

    @POST
    @Path("/dataset")
    @Consumes({ "multipart/form-data" })
    @Produces({ TEXT_URILIST, TEXT_ARFF })
    @Operation(
        summary = "Download dataset and convert into weka arff format.",
        description = "Download an external dataset and convert it into weka arff format. " + SAVE_DATASSET_NOTE,
        tags={ "dataset", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Dataset")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:URI")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")})
        })
    @GroupedApiResponsesOk
    public Response create(
        @Parameter(schema = @Schema(description = "URI of the dataset to be used.", example = "https://api-jaqpot.prod.openrisknet.org/jaqpot/services/dataset/Gajewicz_10_29_class", required=true)) @FormDataParam("dataset_uri") String dataset_uri,
        @Parameter(schema = @Schema(description = "URI of the feature to define as weka class")) @FormDataParam("class_uri") String class_uri,
        @Parameter(description = "Authorization token" ) @HeaderParam("subjectid") String subjectid,
        @Parameter(description = "Token for Bearer Authentication", example = "Bearer eyJhbGciO...") @HeaderParam("bearerToken") String bearerToken,
        @Context HttpHeaders headers, @Context UriInfo ui) throws ApiException {

            Dataset ds = DatasetService.readExternalDataset(dataset_uri, subjectid, bearerToken);
            if (ds.datasetUri == null) ds.datasetUri = dataset_uri;
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
    @Operation(
        summary = "List all converted datasets.",
        description = "List all converted datasets.",
        tags={ "dataset", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:URIList")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:URIList")})
        })
    @GroupedApiResponsesOk
    public Response list(
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

            String accept = headers.getRequestHeaders().getFirst("accept");
            Object datasetList = DatasetService.listDatasets(ui, accept, subjectid);

            return Response
                .ok(datasetList)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/dataset/{id}")
    @Produces({ TEXT_ARFF })
    @Operation(
        summary = "Get arff representation of a dataset.",
        description = "Get arff representation of a dataset.",
        tags={ "dataset", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Dataset")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Arff")})
        })
    @GroupedApiResponsesOk
    public Response getDatasetArff(
        @Parameter(description = "Dataset ID" )@PathParam("id") String id,
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui)
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
    @Operation(
        summary = "Filter an internal dataset with weka filter.",
        description = "Filter an internal dataset with weka filter. Remove attributes and normalize or standardize all numeric attributes " +
            "of a dataset. Change String to Nominal. Or Discretize attributes. " + SAVE_DATASSET_NOTE,
        tags={ "dataset" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/dataset/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Dataset")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Dataset")})
        })
    @GroupedApiResponsesOk
    public Response filter(
        @Parameter(description = "Dataset ID" )@PathParam("id") String id,
        @Parameter(description = "Feature(s) to remove. ID or comma separated IDs of the attribute(s)(column(s)) to remove. (0 = no attribute will be deleted)",
            schema = @Schema(defaultValue = "0"))@FormDataParam("idx_remove") String idx_remove,
        @Parameter(description = "Normalize all numeric values - scale: - The factor for scaling the output range (default: 1).")@FormDataParam("scale") String scale,
        @Parameter(description = "Normalize all numeric values - translation: The translation of the output range (default: 0).")@FormDataParam("translation") String translation,
        @Parameter(description = "Standardize all numeric attributes in the given dataset to have zero mean and unit variance (apart from the class attribute, if set).")@FormDataParam("standardize") Boolean standardize,
        @Parameter(description = "Ignore class (ignore class attribute for Normalization or Standization).")@FormDataParam("ignore") Boolean ignore,
        @Parameter(description = "String to Nominal: Sets which attributes to process. This attributes must be string attributes (\"first\" and \"last\" are valid values as well as ranges and lists. Empty value do not process the filter).")@FormDataParam("attributeRange") String attributeRange,
        @Parameter(description = "Discretize: Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\". Empty value do not process the filter).")@FormDataParam("attributeIndicies") String attributeIndicies,
        @Parameter(description = "Discretize: Number of bins (default: 10).")@FormDataParam("bins") Integer bins,
        @Parameter(description = "Discretize: useEqualFrequency - if set to true, equal-frequency binning will be used instead of equal-width binning.")@FormDataParam("useEqualFrequency") Boolean useEqualFrequency,
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context HttpHeaders headers, @Context UriInfo ui ) throws Exception{

        String accept = headers.getRequestHeaders().getFirst("accept");
        Dataset ds = DatasetService.getDataset(id, subjectid);
        String uri = ui.getBaseUri().toString();

        String output = DatasetService.filter(ds, idx_remove, scale, translation, standardize, ignore, attributeRange, attributeIndicies, bins, useEqualFrequency, accept, uri);

        return Response
                .ok(output)
                .status(Response.Status.OK)
                .build();
    }


    //dataset structure
    public String URI;
    public String datasetUri;
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
