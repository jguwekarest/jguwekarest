package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.NotFoundException;
import io.swagger.api.factories.TreesFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;


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
    @Produces({ "text/x-arff" })

    @ApiOperation(value = "", notes = "REST interface to the WEKA J48 classifier.", response = void.class, tags={ "algorithm", }, position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmJ48Post(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            ,@ApiParam(value = "Whether to use binary splits on nominal attributes when building the trees.", allowableValues = "0, 1", defaultValue="0")@FormDataParam("binarySplits") Integer binarySplits
            ,@ApiParam(value = "The confidence factor used for pruning (smaller values incur more pruning).", defaultValue = "0.25")@FormDataParam("confidenceFactor") BigDecimal confidenceFactor
            ,@ApiParam(value = "The minimum number of instances per leaf.", defaultValue = "2")@FormDataParam("minNumObj") Integer minNumObj
            ,@ApiParam(value = "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree", defaultValue = "3")@FormDataParam("numFolds") Integer numFolds
            ,@ApiParam(value = "Whether reduced-error pruning is used instead of C.4.5 pruning.", allowableValues="0, 1", defaultValue = "0")@FormDataParam("reducedErrorPruning") Integer reducedErrorPruning
            ,@ApiParam(value = "The seed used for randomizing the data when reduced-error pruning is used.", allowableValues="0, 1", defaultValue = "1")@FormDataParam("seed") Integer seed
            ,@ApiParam(value = "Whether to consider the subtree raising operation when pruning.", allowableValues="0, 1", defaultValue = "1")@FormDataParam("subtreeRaising") Integer subtreeRaising
            ,@ApiParam(value = "Whether pruning is performed.", defaultValue = "0", allowableValues="0, 1")@FormDataParam("unpruned") Integer unpruned
            ,@ApiParam(value = "Whether counts at leaves are smoothed based on Laplace.", defaultValue = "0", allowableValues="0, 1")@FormDataParam("useLaplace") Integer useLaplace
            ,@Context SecurityContext securityContext)
            throws NotFoundException, IOException {
        return delegate.algorithmJ48Post(fileInputStream, fileDetail,binarySplits,confidenceFactor,minNumObj,numFolds,reducedErrorPruning,seed,subtreeRaising,unpruned,useLaplace,securityContext,servletContext);
    }

}
