package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.BayesService;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskHandler;
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
        String subjectid = headers.getRequestHeaders().getFirst("subjectid");

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);
        String baseuri = ui.getBaseUri().toString();

        TaskHandler task = new TaskHandler(BayesNet.class.toString(), "BayesNet", "Training data on BayesNet algorithm.", baseuri) {
            @Override
            public void run() {
                try {
                    setState(Task.Step.PREPARATION, 10f);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("datasetUri", datasetUri);
                    params.put("estimator", estimator);
                    params.put("estimatorParams", estimatorParams);
                    params.put("estimator", estimator);
                    params.put("useADTree", useADTree);
                    params.put("searchAlgorithm", searchAlgorithm);
                    params.put("searchParams", searchParams);

                    String[] options = getBayesNetOptions(estimator, estimatorParams, useADTree, searchAlgorithm, searchParams);

                    BayesNet classifier = new BayesNet();

                    Instances instances = WekaUtils.instancesFromString(txtStr, true);

                    try {
                        classifier.setOptions(options);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //return "Error: check options for WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + Arrays.toString(options) + "\"\nWeka error message: " + e.getMessage() + "\n";
                    }
                    setState(Task.Step.TRAINING, 30f);
                    try {
                        classifier.buildClassifier(instances);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //return Response.serverError().entity("Error: WEKA weka.classifiers.bayes.net.search." + searchAlgorithm + "\n parameters: \"" + Arrays.toString(options) + "\"\nWeka error message: " + e.getMessage() + "\n").build();
                    }
                    setState(Task.Step.VALIDATION, 70f);
                    String validation;
                    validation = Validation.crossValidation(instances, classifier);
                    ;
                    Vector<Object> v = new Vector<>();
                    v.add(classifier);
                    v.add(new Instances(instances, 0));

                    //String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
                    String id = ModelService.saveModel(classifier, classifier.getOptions(), params, validation, subjectid);
                    String baseuri = ui.getBaseUri().toString();
                    setResultURI(baseuri + "model/" + id);
                    finish();
                } catch (IOException e) {
                    System.out.println("=============================== \n in der IOExeption \n =============================");
                    e.printStackTrace();
                }
            }
        };
        task.start();
        return Response.ok(task.getURI()).build();
    }
}
