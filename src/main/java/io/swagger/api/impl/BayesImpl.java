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
import java.util.Vector;

public class BayesImpl extends BayesService {
    @Override
    @Produces("text/plain")
    public Response algorithmBayesNetPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, String estimator, BigDecimal estimatorParams, Integer useADTree, String searchAlgorithm, String searchParams, HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException {
        String subjectid = headers.getRequestHeaders().getFirst("subjectid");
        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        String parameters = "";

        if (useADTree != 1) { parameters += " -D ";}

        // Set the parameter for the searchAlgo
        parameters += " -Q ";
        parameters += "weka.classifiers.bayes.net.search." + searchAlgorithm;
        System.out.println("searchAlgorithm is: " + searchAlgorithm);
        // Set the search parameters
        if (searchParams != null) {
            parameters += " -- ";
            parameters += searchParams;
        }
        // Set estimator
        if (estimator != null) {
            parameters += " -E ";
            parameters += "weka.classifiers.bayes.net.estimate." + estimator;
        }
        // Set the parameters for the estimator
        if (estimatorParams != null) {
            parameters += " -- ";
            parameters += " -A ";
            parameters += estimatorParams;
        }
        System.out.println("parameterstring for weka: " + parameters);

        BayesNet classifier = new BayesNet();

        Instances instances = WekaUtils.instancesFromString(txtStr);

        String[] options = new String[0];
        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = Validation.crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));
        try {
            ModelService.saveModel(classifier, classifier.getOptions(), validation,"");
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }
        return Response.ok(v.toString() + "\n" + validation ).build();
    }

}
