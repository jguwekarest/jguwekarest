package io.swagger.api.algorithm;

import io.swagger.annotations.Api;
import io.swagger.api.factories.MetaFactory;

import javax.servlet.ServletConfig;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/algorithm")
@Api(description = "the meta algorithm API")

public class Meta {


    private final MetaService delegate;

    public Meta(@Context ServletConfig servletContext) {
        MetaService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Meta.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (MetaService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = MetaFactory.getMeta();
        }
        this.delegate = delegate;
    }


/*
    @POST
    @Path("/adaboost/j48")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "text/uri-list"})
    @ApiOperation(value = "REST interface to the WEKA Adaboost M1 meta classifier.", notes = "REST interface to the WEKA Adaboost M1 meta classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
            ,extensions = @Extension(name = "algorithm", properties = { @ExtensionProperty(name = "Adaboost M1 meta algorithm", value = "https://en.wikipedia.org/wiki/AdaBoost")}))
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response algorithmAdaBoostPost(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            , @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri
            , @ApiParam(value = "Whether to use binary splits on nominal attributes when building the trees.", allowableValues = "0, 1", defaultValue="0")@FormDataParam("binarySplits") Integer binarySplits
            , @ApiParam(value = "The confidence factor used for pruning (smaller values incur more pruning).", defaultValue = "0.25")@FormDataParam("confidenceFactor") BigDecimal confidenceFactor
            , @ApiParam(value = "The minimum number of instances per leaf.", defaultValue = "2")@FormDataParam("minNumObj") Integer minNumObj
            , @ApiParam(value = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree", defaultValue = "3")@FormDataParam("numFolds") Integer numFolds
            , @ApiParam(value = "Whether reduced-error pruning is used instead of C.4.5 pruning.", allowableValues="0, 1", defaultValue = "0")@FormDataParam("reducedErrorPruning") Integer reducedErrorPruning
            , @ApiParam(value = "The seed used for randomizing the data when reduced-error pruning is used.", allowableValues="0, 1", defaultValue = "1")@FormDataParam("seed") Integer seed
            , @ApiParam(value = "Whether to consider the subtree raising operation when pruning.", allowableValues="0, 1", defaultValue = "1")@FormDataParam("subtreeRaising") Integer subtreeRaising
            , @ApiParam(value = "Whether pruning is performed.", defaultValue = "1", allowableValues="0, 1")@FormDataParam("unpruned") Integer unpruned
            , @ApiParam(value = "Whether counts at leaves are smoothed based on Laplace.", defaultValue = "0", allowableValues="0, 1")@FormDataParam("useLaplace") Integer useLaplace
            , @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid
            , @Context UriInfo ui, @Context HttpHeaders headers)
            throws io.swagger.api.NotFoundException, IOException {
        return delegate.algorithmAdaBoostPost(fileInputStream,fileDetail,datasetUri,subjectid,headers,ui);
    }
*/



}
