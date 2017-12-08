package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.TreesService;
import io.swagger.api.data.DatasetService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.Vector;

public class TreesImpl extends TreesService {

    @Override
    @Produces("text/plain")
    public Response algorithmJ48Post(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds, Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned, Integer useLaplace, SecurityContext securityContext, ServletContext servletContext, HttpHeaders headers) throws NotFoundException, IOException {

        Object[] params = {binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace};

        for (Object param : params) {
            System.out.println("param are: " + param);
        }
        String subjectid = headers.getRequestHeaders().getFirst("subjectid");
        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        String parameters = "";

        if (binarySplits == 1) {
            parameters += " -B ";
        }

        if (minNumObj != null) {
            parameters += " -M " + minNumObj;
        } else {
            parameters += " -M 2 ";
        }

        if (reducedErrorPruning == 1) {
            if (numFolds != null) {
                parameters += " -R -N " + numFolds;
            } else {
                parameters += " -R -N 3 ";
            }
        }

        if (seed != null) {
            parameters += " -Q " + seed;
        } else {
            parameters += " -Q 2 ";
        }

        if (unpruned == 1) {
            parameters += " -U ";
        } else {
            if (subtreeRaising == 0) {
                parameters += " -S ";
            }
            if (confidenceFactor != null) {
                parameters += " -C " + confidenceFactor;
            } else {
                parameters += " -C 0.25 ";
            }
        }

        if (useLaplace !=null && useLaplace == 1) {
            parameters += " -A ";
        }

        System.out.println("parameterstring for weka: weka.classifiers.trees.J48 " + parameters);

        J48 classifier = new J48();
        String[] options;

        Instances instances = WekaUtils.instancesFromString(txtStr);

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.trees.j48\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.trees.j48\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = Validation.crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        String contextBasePath = servletContext.getRealPath("/");
        if (WekaUtils.saveWekaModel(v, contextBasePath + "/j48.model")) {
            System.out.println("Model is saved to ");
        }
        return Response.ok(v.toString() + "\n" + validation + "\n").build();
    }

    public void showParams(TreeMap<String, String> parameters){
        System.out.println(parameters);
    }

}
