package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.BayesService;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import static io.swagger.api.WekaOptionHelper.getBayesNetOptions;

public class BayesImpl extends BayesService {
    @Override
    @Produces("text/plain")
    public Response algorithmBayesNetPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, String estimator,
                                          BigDecimal estimatorParams, Integer useADTree, String searchAlgorithm, String searchParams,
                                          HttpHeaders headers, UriInfo ui, SecurityContext securityContext)
            throws NotFoundException, IOException {

        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("datasetUri", datasetUri);
        params.put("estimator", estimator);
        params.put("estimatorParams", estimatorParams);
        params.put("estimator", estimator);
        params.put("useADTree", useADTree);
        params.put("searchAlgorithm", searchAlgorithm);
        params.put("searchParams", searchParams);

        String subjectid  = headers.getRequestHeaders().getFirst("subjectid");

        String txtStr     = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        String[] options = getBayesNetOptions(estimator, estimatorParams, useADTree, searchAlgorithm, searchParams);

        BayesNet classifier = new BayesNet();

        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + options.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + options.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation;
        validation = Validation.crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        //if(save != null && save) ModelService.saveModel(classifier, classifier.getOptions(), validation, subjectid);
        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(classifier, classifier.getOptions(), params, validation, subjectid);
            String baseuri = ui.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }

    }

}
