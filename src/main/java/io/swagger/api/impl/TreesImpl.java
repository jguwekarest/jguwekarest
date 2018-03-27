package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.algorithm.TreesService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class TreesImpl extends TreesService {

    @Override
    @Produces("text/plain")
    public Response algorithmJ48Post(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer binarySplits,
                                     BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds, Integer reducedErrorPruning, Integer seed,
                                     Integer subtreeRaising, Integer unpruned, Integer useLaplace, String subjectid, HttpHeaders headers, UriInfo uriInfo
                                    ) throws NotFoundException, IOException {

        /*HashMap<String,Object> params = new HashMap<>();
        params.put("datasetUri", datasetUri);
        params.put("binarySplits", binarySplits);
        params.put("confidenceFactor", confidenceFactor);
        params.put("minNumObj", minNumObj);
        params.put("numFolds", numFolds);
        params.put("reducedErrorPruning", reducedErrorPruning);
        params.put("seed", seed);
        params.put("subtreeRaising", subtreeRaising);
        params.put("unpruned", unpruned);
        params.put("useLaplace", useLaplace);

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);
        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        String[] options = getJ48Options(binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace);

        J48 classifier = new J48();

        try {
            classifier.setOptions(options);
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.trees.j48\n parameters: \"" + Arrays.toString(options) + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = Validation.crossValidation(instances, classifier);

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
*/
        return null;
    }


    @Produces("text/plain")
    public Response algorithmJ48AdaBoostPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer batchSize, Integer numIterations,
                                     Integer useResampling, Integer weightThreshold, Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj,
                                     Integer numFolds, Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned,
                                     Integer useLaplace, String subjectid, HttpHeaders headers, UriInfo uriInfo
                                     ) throws NotFoundException, IOException {

/*        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);
        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        String[] options = getJ48Options(binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace);
        String[] adaOptions = getAdaBoostOptions(batchSize, numIterations, useResampling, weightThreshold);

        AdaBoostM1 adaBoost = new AdaBoostM1();
        J48 classifier = new J48();

        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.trees.j48\n parameters: \"" + Arrays.toString(options) + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        try {
            if(adaOptions != null) adaBoost.setOptions(adaOptions);
            adaBoost.setClassifier(classifier);
            adaBoost.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.meta.adaboost\n parameters: \"" + Arrays.toString(adaOptions) + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = Validation.crossValidation(instances, adaBoost);

        Vector<Object> v = new Vector<>();
        v.add(adaBoost);
        v.add(new Instances(instances, 0));

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(adaBoost, ArrayUtils.addAll(adaBoost.getOptions(), classifier.getOptions()), validation, subjectid);
            String baseuri = uriInfo.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }*/
return null;
    }


    @Produces("text/plain")
    public Response algorithmJ48BaggingPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer bagSizePercent,
                                            Integer batchSize, Integer numIterations, Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj,
                                             Integer numFolds, Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned,
                                             Integer useLaplace, String subjectid, HttpHeaders headers, UriInfo uriInfo
    ) throws NotFoundException, IOException {

     /*   String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);
        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        String[] options = getJ48Options(binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace);
        String[] bagginOptions = getBaggingOptions(bagSizePercent, batchSize, numIterations);

        Bagging bagging = new Bagging();
        J48 classifier = new J48();

        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.trees.j48\n parameters: \"" + Arrays.toString(options) + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        try {
            if(bagginOptions != null)bagging.setOptions(bagginOptions);
            bagging.setClassifier(classifier);
            bagging.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.meta.bagging\n parameters: \"" + Arrays.toString(bagginOptions) + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = Validation.crossValidation(instances, bagging);

        Vector<Object> v = new Vector<>();
        v.add(bagging);
        v.add(new Instances(instances, 0));

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(bagging, ArrayUtils.addAll(bagging.getOptions(), classifier.getOptions()), validation, subjectid);
            String baseuri = uriInfo.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }*/
return null;
    }


    /* public void showParams(TreeMap<String, String> parameters){
        System.out.println(parameters);
    } */

}
