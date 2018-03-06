package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.RulesService;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import static io.swagger.api.WekaOptionHelper.getM5RuleOptions;
import static io.swagger.api.impl.Validation.crossValidation;

public class RulesImpl extends RulesService {

    @Override
    @Produces("text/plain")
    public Response algorithmZeroRPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                       String subjectid, HttpHeaders headers, UriInfo uriInfo) throws NotFoundException, IOException {

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        ZeroR classifier = new ZeroR();
        //String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.ZeroR\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(classifier, classifier.getOptions(), validation, subjectid);
            String baseuri = uriInfo.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }
    }

    @Produces("text/plain")
    public Response algorithmM5RulesPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                         Integer unpruned, Integer useUnsmoothed, Double minNumInstances, Integer buildRegressionTree,
                                         String subjectid, HttpHeaders headers, UriInfo uriInfo)
            throws Exception {

        HashMap<String,Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("unpruned", unpruned);
        params.put("useUnsmoothed", useUnsmoothed);
        params.put("minNumInstances", minNumInstances);
        params.put("buildRegressionTree", buildRegressionTree);

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        M5Rules classifier = new M5Rules();

        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        String[] options = getM5RuleOptions(unpruned, useUnsmoothed, minNumInstances, buildRegressionTree);

        try {
            classifier.setOptions(options);
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.M5Rules\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);

        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(classifier, classifier.getOptions(), params, validation, subjectid);
            String baseuri = uriInfo.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }

    }
}
