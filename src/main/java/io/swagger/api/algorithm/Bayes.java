package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.factories.BayesFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;


@Path("/algorithm")

@Api(description = "the bayes algorithm API")

public class Bayes {
    private final BayesService delegate;

    public Bayes(@Context ServletConfig servletContext) {
        BayesService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Bayes.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (BayesService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = BayesFactory.getBayes();
        }
        this.delegate = delegate;
    }



    @POST
    @Path("/BayesNet")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })
    @ApiOperation(value = "REST interface to the WEKA BayesNet classifier.", notes = "REST interface to the WEKA BayesNet classifier.", response = void.class, tags={ "algorithm", }, position = 0)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmBayesNetPost(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            ,@ApiParam(value = "The estimator algorithm to be used in the compound. Must be SimpleEstimator,  MultiNomialBMAEstimator, BMAEstimator or BayesNetEstimator (Default: SimpleEstimator).", allowableValues="SimpleEstimator, MultiNomialBMAEstimator, BMAEstimator, BayesNetEstimator", defaultValue="SimpleEstimator")@FormDataParam("estimator")  String estimator
            ,@ApiParam(value = "The parameter for the estimator to be used in the compound.  Must be of type double (Default: 0.5).", defaultValue="0.5")@FormDataParam("estimatorParams") BigDecimal estimatorParams
            ,@ApiParam(value = "Whether to use ADTrees for searching (using will increase the speed of the search, but will also raise the memory use (Default: 0).", allowableValues="0, 1", defaultValue="0")@FormDataParam("useADTree") Integer useADTree
            ,@ApiParam(value = "The algorithmn to be used for searching in the compound. Must be local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm (Default: local.K2).", allowableValues="local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm", defaultValue="local.K2")@FormDataParam("searchAlgorithm")  String searchAlgorithm
            ,@ApiParam(value = "The parameter for algorithmn to be used for searching in the compound. Are set automatically (WEKA's standard parameter setting).", defaultValue="-P 1 -S BAYES")@FormDataParam("searchParams")  String searchParams
            ,@ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid
            ,@Context SecurityContext securityContext)
            throws io.swagger.api.NotFoundException, IOException {
        return delegate.algorithmBayesNetPost(fileInputStream, fileDetail,estimator,estimatorParams,useADTree,searchAlgorithm,searchParams,securityContext);
    }


}
