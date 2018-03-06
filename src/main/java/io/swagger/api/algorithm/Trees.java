package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.TreesFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;


@Path("/algorithm")

@Api(description = "the trees algorithm API")

public class Trees  {
    private final TreesService delegate;

    public Trees(@Context ServletConfig servletContext) {
        TreesService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("AlgorithmApi.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (TreesService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = TreesFactory.getTrees();
        }
        this.delegate = delegate;
    }


    @Context ServletContext servletContext;
    @POST
    @Path("/J48")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "text/uri-list" })
    @ApiOperation(value = "REST interface to the WEKA J48 classifier.",
        notes = "REST interface to the WEKA J48 classifier. " + SAVE_MODEL_NOTE,
        tags={ "algorithm" },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset") }),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model") }),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "J48", value = "https://en.wikipedia.org/wiki/C4.5_algorithm#Implementations")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response algorithmJ48Post(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "Whether to use binary splits on nominal attributes when building the trees.", allowableValues = "0, 1", defaultValue="0")@FormDataParam("binarySplits") Integer binarySplits,
        @ApiParam(value = "The confidence factor used for pruning (smaller values incur more pruning).", defaultValue = "0.25")@FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @ApiParam(value = "The minimum number of instances per leaf.", defaultValue = "2")@FormDataParam("minNumObj") Integer minNumObj,
        @ApiParam(value = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree", defaultValue = "3")@FormDataParam("numFolds") Integer numFolds,
        @ApiParam(value = "Whether reduced-error pruning is used instead of C.4.5 pruning.", allowableValues="0, 1", defaultValue = "0")@FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @ApiParam(value = "The seed used for randomizing the data when reduced-error pruning is used.", defaultValue = "1")@FormDataParam("seed") Integer seed,
        @ApiParam(value = "Whether to consider the subtree raising operation when pruning.", allowableValues="0, 1", defaultValue = "1")@FormDataParam("subtreeRaising") Integer subtreeRaising,
        @ApiParam(value = "Whether pruning is performed.", defaultValue = "1", allowableValues="0, 1")@FormDataParam("unpruned") Integer unpruned,
        @ApiParam(value = "Whether counts at leaves are smoothed based on Laplace.", defaultValue = "0", allowableValues="0, 1")@FormDataParam("useLaplace") Integer useLaplace,
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo uriInfo, @Context HttpHeaders headers)
        throws NotFoundException, IOException {
        return delegate.algorithmJ48Post(fileInputStream, fileDetail,datasetUri,binarySplits,confidenceFactor,minNumObj,numFolds,reducedErrorPruning,
            seed,subtreeRaising,unpruned,useLaplace,subjectid,headers,uriInfo);
    }


    @POST
    @Path("/J48/adaboost")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "text/uri-list" })
    @ApiOperation(value = "REST interface to the WEKA Adaboost M1 meta classifier.",
        notes = "REST interface to the WEKA Adaboost M1 meta classifier. " + SAVE_MODEL_NOTE,
        tags = {"algorithm","meta algorithm"} ,
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48/adaboost")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
            @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
            @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
            @Extension(name = "algorithm", properties = {
                @ExtensionProperty(name = "Adaboost M1 meta algorithm", value = "https://en.wikipedia.org/wiki/AdaBoost")
            })
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found")})
    public Response algorithmJ48AdaBoostPost(
        //data params
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,@ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).") @FormDataParam("datasetURI") String datasetUri,
        //meta params,
        @ApiParam(value = "Adaboost M1: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.", defaultValue = "100") @FormDataParam("batchSize") Integer batchSize,
        @ApiParam(value = "Adaboost M1: The number of iterations to be performed.", defaultValue = "10") @FormDataParam("numIterations") Integer numIterations,
        @ApiParam(value = "Adaboost M1: Whether resampling is used instead of reweighting.", defaultValue = "0", allowableValues = "0, 1") @FormDataParam("useResampling") Integer useResampling,
        @ApiParam(value = "Adaboost M1: Weight threshold for weight pruning.", defaultValue = "100") @FormDataParam("weightThreshold") Integer weightThreshold,
        //J48,
        @ApiParam(value = "Whether to use binary splits on nominal attributes when building the trees.", allowableValues = "0, 1", defaultValue = "0") @FormDataParam("binarySplits") Integer binarySplits,
        @ApiParam(value = "The confidence factor used for pruning (smaller values incur more pruning).", defaultValue = "0.25") @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @ApiParam(value = "The minimum number of instances per leaf.", defaultValue = "2") @FormDataParam("minNumObj") Integer minNumObj,
        @ApiParam(value = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree", defaultValue = "3") @FormDataParam("numFolds") Integer numFolds,
        @ApiParam(value = "Whether reduced-error pruning is used instead of C.4.5 pruning.", allowableValues = "0, 1", defaultValue = "0") @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @ApiParam(value = "The seed used for randomizing the data when reduced-error pruning is used.", defaultValue = "1") @FormDataParam("seed") Integer seed,
        @ApiParam(value = "Whether to consider the subtree raising operation when pruning.", allowableValues = "0, 1", defaultValue = "1") @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @ApiParam(value = "Whether pruning is performed.", defaultValue = "1", allowableValues = "0, 1") @FormDataParam("unpruned") Integer unpruned,
        @ApiParam(value = "Whether counts at leaves are smoothed based on Laplace.", defaultValue = "0", allowableValues = "0, 1") @FormDataParam("useLaplace") Integer useLaplace,
        //general params,
        @ApiParam(value = "Authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo uriInfo, @Context HttpHeaders headers)
        throws NotFoundException, IOException {
        return delegate.algorithmJ48AdaBoostPost(fileInputStream, fileDetail, datasetUri, batchSize, numIterations, useResampling, weightThreshold,
            binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace,
            subjectid, headers, uriInfo);
    }


    @POST
    @Path("/J48/bagging")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "text/uri-list" })
    @ApiOperation(value = "REST interface to the WEKA Adaboost M1 meta classifier.", notes = "REST interface to the WEKA Adaboost M1 meta classifier. " + SAVE_MODEL_NOTE, tags = {"algorithm","meta algorithm"},
        extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/J48/bagging")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),
        @Extension(name = "algorithm", properties = {@ExtensionProperty(name = "Bagging meta algorithm", value = "https://en.wikipedia.org/wiki/Bootstrap_aggregating")})
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found")})
    public Response algorithmJ48BaggingPost(
        //data params
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).") @FormDataParam("datasetURI") String datasetUri,
        //meta params,
        @ApiParam(value = "Bagging: Size of each bag, as a percentage of the training set size.", defaultValue = "100") @FormDataParam("bagSizePercent") Integer bagSizePercent,
        @ApiParam(value = "Bagging: The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.", defaultValue = "100") @FormDataParam("batchSize") Integer batchSize,
        @ApiParam(value = "Bagging: The number of iterations to be performed.", defaultValue = "10") @FormDataParam("numIterations") Integer numIterations,
        //J48,
        @ApiParam(value = "Whether to use binary splits on nominal attributes when building the trees.", allowableValues = "0, 1", defaultValue = "0") @FormDataParam("binarySplits") Integer binarySplits,
        @ApiParam(value = "The confidence factor used for pruning (smaller values incur more pruning).", defaultValue = "0.25") @FormDataParam("confidenceFactor") BigDecimal confidenceFactor,
        @ApiParam(value = "The minimum number of instances per leaf.", defaultValue = "2") @FormDataParam("minNumObj") Integer minNumObj,
        @ApiParam(value = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree", defaultValue = "3") @FormDataParam("numFolds") Integer numFolds,
        @ApiParam(value = "Whether reduced-error pruning is used instead of C.4.5 pruning.", allowableValues = "0, 1", defaultValue = "0") @FormDataParam("reducedErrorPruning") Integer reducedErrorPruning,
        @ApiParam(value = "The seed used for randomizing the data when reduced-error pruning is used.", defaultValue = "1") @FormDataParam("seed") Integer seed,
        @ApiParam(value = "Whether to consider the subtree raising operation when pruning.", allowableValues = "0, 1", defaultValue = "1") @FormDataParam("subtreeRaising") Integer subtreeRaising,
        @ApiParam(value = "Whether pruning is performed.", defaultValue = "1", allowableValues = "0, 1") @FormDataParam("unpruned") Integer unpruned,
        @ApiParam(value = "Whether counts at leaves are smoothed based on Laplace.", defaultValue = "0", allowableValues = "0, 1") @FormDataParam("useLaplace") Integer useLaplace,
        //general params,
        @ApiParam(value = "Authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo uriInfo, @Context HttpHeaders headers)
        throws NotFoundException, IOException {
        return delegate.algorithmJ48BaggingPost(fileInputStream, fileDetail, datasetUri, bagSizePercent, batchSize, numIterations,
            binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace,
            subjectid, headers, uriInfo);
    }

}