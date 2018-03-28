package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static io.swagger.api.Constants.TEXT_URILIST;
import static io.swagger.api.WekaOptionHelper.getClassifierOptions;

public class AlgorithmImpl extends AlgorithmService {

    @Override
    public Response algorithmAlgorithmnameGet(String accept, String algorithmname, String subjectid, SecurityContext securityContext, ServletContext servletContext) throws IOException {
        String jsonString;
        try {
            String contextBasePath = servletContext.getRealPath("/");
            jsonString = new String(Files.readAllBytes(Paths.get(contextBasePath + "/swagger.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Response.ok(jsonString).build();
    }

    @Override
    public Response algorithmAlgorithmnamePost(String identifier, String algorithmname, String subjectid, SecurityContext securityContext) throws NotFoundException {
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }


    @Override
    public Response algorithmGet(String accept, String subjectid, SecurityContext securityContext, UriInfo ui) throws NotFoundException, IOException {
        String baseuri = ui.getBaseUri().toString();
        InputStream in = new URL( ui.getBaseUri() + "swagger.json" ).openStream();
        String jsonContent;
        try {
            jsonContent = IOUtils.toString(in, "UTF-8");
        } finally {
            IOUtils.closeQuietly(in);
        }

        JSONObject apiObject  = new JSONObject(jsonContent);
        //sort the JSONObject somehow
        StringBuilder output = new StringBuilder();
        JSONObject paths = apiObject.getJSONObject("paths");
        JSONObject jsonout = new JSONObject();

        Iterator<?> keys = paths.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if ( paths.get(key) instanceof JSONObject ) {
                JSONObject method = (JSONObject) paths.get(key);
                Iterator<?> methodKeys =  method.keys();
                while( methodKeys.hasNext() ) {
                    String methodVal = (String)methodKeys.next();
                    System.out.println("methodVal is:" + methodVal);
                    if (!key.contains("{") && key.startsWith("/algorithm/")) {
                        output.append("").append(StringUtil.removeTrailingSlash(baseuri)).append(key).append("\n");
                        if (accept.equals(MediaType.APPLICATION_JSON)) jsonout.put( StringUtil.removeTrailingSlash(baseuri) + key, paths.getJSONObject(key));
                    }
                }
            }
        }

        if (accept.equals(MediaType.APPLICATION_JSON)) return Response.ok(jsonout.toString()).build();
        return Response.ok(output.toString()).build();
    }


    /**
     * Method overload to: Train a classifier or meta classifier - without metaClassifierName and metaParams for meta classifier
     * @param fileInputStream dataset file handle
     * @param fileDetail dataset file details
     * @param datasetUri dataset URI
     * @param classifierName String classifier name
     * @param params HashMap hashed params for classifier
     * @param headers HTTP REST call headers
     * @param ui UriInfo
     * @param securityContext security context
     * @return Task URI
     * @throws NotFoundException file not found
     * @throws IOException io exception
     */
    @Produces("text/plain")
    public Response algorithmPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                  String classifierName, HashMap params, HttpHeaders headers, UriInfo ui, SecurityContext securityContext)throws NotFoundException, IOException {
        return algorithmPost(fileInputStream, fileDetail, datasetUri, classifierName, params, null, null, headers, ui, securityContext);
    }

    /**
     * Train a classifier or meta classifier
     * @param fileInputStream dataset file handle
     * @param fileDetail dataset file details
     * @param datasetUri dataset URI
     * @param classifierName String classifier name
     * @param params HashMap hashed params for classifier
     * @param metaClassifierName Sting optional meta classifier name
     * @param metaParams HashMap optional hashed params for meta classifier
     * @param headers HTTP REST call headers
     * @param ui UriInfo
     * @param securityContext security context
     * @return Task URI
     * @throws NotFoundException file not found
     * @throws IOException io exception
     */
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    public Response algorithmPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                  String classifierName, HashMap params, String metaClassifierName, HashMap metaParams,
                                  HttpHeaders headers, UriInfo ui, SecurityContext securityContext)
        throws NotFoundException, IOException {

        String subjectid = headers.getRequestHeaders().getFirst("subjectid");
        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);
        String baseuri = ui.getBaseUri().toString();
        String accept = headers.getRequestHeaders().getFirst("accept");

        TaskHandler task = new TaskHandler(classifierName, classifierName + " algorithm", "Training data on "+ classifierName + " algorithm.", baseuri) {
            AbstractClassifier classifier;
            SingleClassifierEnhancer metaClassifier = null;
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);  //wait until task is saved in mongoDB
                    setState(Task.Step.PREPARATION, 10f);
                    String[] options = getClassifierOptions(classifierName, params);
                    Instances instances = WekaUtils.instancesFromString(txtStr, true);
                    String id = "";
                    Vector<Object> v = new Vector<>();
                    try {
                        switch (classifierName) {
                            case "BayesNet":
                                classifier = new BayesNet();
                                break;
                            case "NaiveBayes":
                                classifier = new NaiveBayes();
                                break;
                            case "LinearRegression":
                                classifier = new LinearRegression();
                                break;
                            case "LibSVM":
                                classifier = new LibSVM();
                                break;
                            case "J48":
                                classifier = new J48();
                                break;
                            case "KNN":
                                classifier = new IBk();
                                break;
                            case "ZeroR":
                                classifier = new ZeroR();
                                break;
                            case "M5Rules":
                                classifier = new M5Rules();
                                break;
                        }
                    } catch (Exception e) {
                        setErrorReport(e,500, "algorithmPost: " +  classifierName);
                        e.printStackTrace();
                    }

                    classifier.setOptions(options);

                    if(metaClassifierName != null) {
                        String[] metaOptions = getClassifierOptions(metaClassifierName, metaParams);
                        switch(metaClassifierName) {
                            case "AdaBoost":
                                metaClassifier = new AdaBoostM1();
                                break;
                            case "Bagging":
                                metaClassifier = new Bagging();
                                break;
                        }
                        metaClassifier.setOptions(metaOptions);
                        metaClassifier.setClassifier(classifier);
                    }

                    setState(Task.Step.TRAINING, 30f);

                    if(metaClassifierName != null) {
                        metaClassifier.buildClassifier(instances);
                        setState(Task.Step.VALIDATION, 70f);
                        String validation = Validation.crossValidation(instances, metaClassifier);
                        v.add(metaClassifier);
                        v.add(new Instances(instances, 0));
                        id = ModelService.saveModel(metaClassifier, ArrayUtils.addAll(metaClassifier.getOptions(), classifier.getOptions()), params, validation, subjectid);
                    } else {
                        classifier.buildClassifier(instances);
                        setState(Task.Step.VALIDATION, 70f);
                        String validation = Validation.crossValidation(instances, classifier);
                        v.add(classifier);
                        v.add(new Instances(instances, 0));
                        id = ModelService.saveModel(classifier, classifier.getOptions(), params, validation, subjectid);
                    }
                    String baseuri = ui.getBaseUri().toString();
                    setResultURI(baseuri + "model/" + id);
                    finish();
                } catch (Exception e) {
                    setErrorReport(e,500, "algorithmPost: " +  classifierName);
                    e.printStackTrace();
                }
            }
        };
        task.start();
        if (accept.equals(TEXT_URILIST)) {
            return Response.ok(task.getURI()).build();
        } else {
            return Response.ok(task).build();
        }

    }

}
