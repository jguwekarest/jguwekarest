package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.api.factories.BayesFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static io.swagger.api.Constants.SAVE_MODEL_NOTE;


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
    @Produces({ "text/x-arff", "text/uri-list" })
    @ApiOperation(value = "REST interface to the WEKA BayesNet classifier.", notes = "REST interface to the WEKA BayesNet classifier. " + SAVE_MODEL_NOTE, tags={ "algorithm", }
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/BayesNet")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:expects", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Dataset")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:Model")}),

        @Extension(name = "algorithm", properties = {
            @ExtensionProperty(name = "http://purl.obolibrary.org/obo/IAO_0000064",  value = "http://purl.enanomapper.org/onto/ENM_8000001"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000001",value = "http://purl.enanomapper.org/onto/ENM_8000002"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000002",value = "http://purl.enanomapper.org/onto/ENM_8000005"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000005",value = "http://purl.enanomapper.org/onto/ENM_8000014"),
            @ExtensionProperty(name = "http://purl.enanomapper.org/onto/ENM_8000014",value = "https://en.wikipedia.org/wiki/Bayesian_network"),
            @ExtensionProperty(name = "BayesNet", value = "https://en.wikipedia.org/wiki/Bayesian_network"),
        })}
    )
    @GroupedApiResponsesOk

    public Response algorithmBayesNetPost(
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @ApiParam(value = "Dataset URI or local dataset ID (to the arff representation of a dataset).")@FormDataParam("datasetURI")  String datasetUri,
        @ApiParam(value = "The estimator algorithm to be used in the compound. Must be SimpleEstimator,  MultiNomialBMAEstimator, BMAEstimator or BayesNetEstimator (Default: SimpleEstimator).", allowableValues="SimpleEstimator, MultiNomialBMAEstimator, BMAEstimator, BayesNetEstimator", defaultValue="SimpleEstimator")@FormDataParam("estimator")  String estimator,
        @ApiParam(value = "The parameter for the estimator to be used in the compound.  Must be of type double (Default: 0.5).", defaultValue="0.5")@FormDataParam("estimatorParams") BigDecimal estimatorParams,
        @ApiParam(value = "Whether to use ADTrees for searching (using will increase the speed of the search, but will also raise the memory use (Default: 0).", allowableValues="0, 1", defaultValue="0")@FormDataParam("useADTree") Integer useADTree,
        @ApiParam(value = "The algorithmn to be used for searching in the compound. Must be local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm (Default: local.K2).", allowableValues="local.K2, local.GeneticSearch, local.HillClimber, local.LAGDHillClimber, local.RepeatedHillClimber, local.SimulatedAnnealing, local.TabuSearch, local.TAN, global.K2, global.GeneticSearch, global.HillClimber, global.RepeatedHillClimber, global.SimulatedAnnealing, global.TabuSearch, global.TAN, ci.CISearchAlgorithm, ci.ICSSearchAlgorithm", defaultValue="local.K2")@FormDataParam("searchAlgorithm")  String searchAlgorithm,
        @ApiParam(value = "The parameter for algorithmn to be used for searching in the compound. Are set automatically (WEKA's standard parameter setting).", defaultValue="-P 1 -S BAYES")@FormDataParam("searchParams")  String searchParams,
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers, @Context SecurityContext securityContext)
        throws io.swagger.api.NotFoundException, IOException {
               return delegate.algorithmBayesNetPost(fileInputStream, fileDetail, datasetUri, estimator, estimatorParams, useADTree, searchAlgorithm, searchParams, headers, ui, securityContext);
    }
}
