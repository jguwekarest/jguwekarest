package io.swagger.api;

import io.swagger.annotations.ApiParam;
import io.swagger.api.factories.TreesFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
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

@io.swagger.annotations.Api(description = "the algorithm API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")

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

    @POST
    @Path("/J48")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "REST interface to the WEKA J48 classifier.", response = void.class, tags={ "algorithm", })
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmJ48Post(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            ,@ApiParam(value = "The estimator algorithm to be used in the compound. Must be SimpleEstimator,  MultiNomialBMAEstimator, BMAEstimator or BayesNetEstimator (Default: SimpleEstimator).", allowableValues="SimpleEstimator, MultiNomialBMAEstimator, BMAEstimator, BayesNetEstimator", defaultValue="SimpleEstimator")@FormDataParam("estimator")  String estimator
            ,@ApiParam(value = "The parameter for the estimator to be used in the compound.  Must be of type double (Default: 0.5).", defaultValue="0.5")@FormDataParam("estimatorParams") BigDecimal estimatorParams
            ,@ApiParam(value = "Whether to use ADTrees for searching (using will increase the speed of the search, but will also raise the memory use (Default: 0).", allowableValues="0, 1", defaultValue="0")@FormDataParam("useADTree") Integer useADTree
            ,@ApiParam(value = "The algorithmn to be used for searching in the compound. Must be local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm (Default: local.K2).", allowableValues="local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm", defaultValue="local.K2")@FormDataParam("searchAlgorithm")  String searchAlgorithm
            ,@ApiParam(value = "The parameter for algorithmn to be used for searching in the compound. Are set automatically (WEKA's standard parameter setting).", defaultValue="-P 1 -S BAYES")@FormDataParam("searchParams")  String searchParams
            ,@Context SecurityContext securityContext)
            throws NotFoundException, IOException {
        return delegate.algorithmJ48NetPost(fileInputStream, fileDetail,estimator,estimatorParams,useADTree,searchAlgorithm,searchParams,securityContext);
    }

}
