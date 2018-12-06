package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Option;

import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.swagger.api.Constants.TEXT_URILIST;
import static io.swagger.api.WekaOptionHelper.getClassifierOptions;

/**
 * Central Class to train WEKA Classifier
 */
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
        InputStream in = new URL( ui.getBaseUri() + "openapi/openapi.json" ).openStream();
        String jsonContent;
        try {
            jsonContent = IOUtils.toString(in, "UTF-8");
        } finally {
            IOUtils.closeQuietly(in);
        }

        JSONObject apiObject = new JSONObject(jsonContent);
        //sort the JSONObject somehow
        StringBuilder output = new StringBuilder();
        JSONObject paths = apiObject.getJSONObject("paths");
        JSONObject jsonout = new JSONObject();

        Iterator<?> keys = paths.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (paths.get(key) instanceof JSONObject) {
                JSONObject method = (JSONObject) paths.get(key);
                Iterator<?> methodKeys = method.keys();
                while (methodKeys.hasNext()) {
                    String methodVal = (String) methodKeys.next();
                    //System.out.println("methodVal is:" + methodVal);
                    if (!key.contains("{") && key.startsWith("/algorithm/")) {
                        output.append("").append(StringUtil.removeTrailingSlash(baseuri)).append(key).append("\n");
                        if (accept.equals(MediaType.APPLICATION_JSON))
                            jsonout.put(StringUtil.removeTrailingSlash(baseuri) + key, paths.getJSONObject(key));
                    }
                }
            }
        }

        if (accept.equals(MediaType.APPLICATION_JSON)) return Response.ok(jsonout.toString()).build();
        return Response.ok(output.toString()).build();
    }

    /**
     * Method overload to: Train a classifier or meta classifier - with parameters already as string
     *
     * @param fileInputStream dataset file handle
     * @param datasetUri      dataset URI
     * @param classifierName  String classifier name
     * @param paramString     param string for classifier as used in WEKA
     * @param validation      validation method (CrossValidation, Hold-Out)
     * @param validationNum   number of CrossValidations or percentage Hold-Out
     * @param headers         HTTP REST call headers
     * @param ui              UriInfo
     * @param securityContext security context
     * @return Task URI
     * @throws NotFoundException file not found
     * @throws IOException       io exception
     */
    @Produces("text/plain")
    public Response algorithmGenericPost(InputStream fileInputStream, String datasetUri,
                                         String classifierName, String paramString, String validation, Double validationNum, HttpHeaders headers, UriInfo ui, SecurityContext securityContext)
        throws NotFoundException, IOException {
        return algorithmPost(fileInputStream, datasetUri, classifierName, null, null, null, paramString,
                             validation, validationNum, headers, ui, securityContext);
    }


    /**
     * Train a classifier or meta classifier
     *
     * @param classifierName  String classifier name
     * @param headers         HTTP REST call headers
     * @param ui              UriInfo
     * @param securityContext security context
     * @return Parameter description
     * @throws NotFoundException URI not found
     */
    @Produces({MediaType.TEXT_PLAIN})
    public Response algorithmGenericGet(String classifierName, HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException {
        AbstractClassifier classifier;
        classifier = getClassifier(classifierName);
        StringBuilder output = new StringBuilder();
        Enumeration<Option> enu = classifier.listOptions();
        while (enu.hasMoreElements()) {
            Option option = enu.nextElement();
            output.append(option.synopsis()).append("\n").append(option.description()).append("\n");
        }
        output.append("\n");
        return Response.ok(output.toString()).build();
    }


    /**
     * Method overload to: Train a classifier or meta classifier - without metaClassifierName and metaParams for meta classifier
     *
     * @param fileInputStream dataset file handle
     * @param datasetUri      dataset URI
     * @param classifierName  String classifier name
     * @param params          HashMap hashed params for classifier
     * @param headers         HTTP REST call headers
     * @param ui              UriInfo
     * @param securityContext security context
     * @return Task URI
     * @throws NotFoundException file not found
     * @throws IOException       io exception
     */
    @Produces("text/plain")
    public Response algorithmPost(InputStream fileInputStream, String datasetUri,
                                  String classifierName, HashMap params, String validation, Double validationNum,
                                  HttpHeaders headers, UriInfo ui, SecurityContext securityContext)
        throws NotFoundException, IOException {
        return algorithmPost(fileInputStream, datasetUri, classifierName, params, null, null, null,
                             validation, validationNum, headers, ui, securityContext);
    }



    /**
     * Method overload to: Train a classifier or meta classifier - without metaClassifierName and metaParams for meta classifier
     *
     * @param fileInputStream dataset file handle
     * @param datasetUri      dataset URI
     * @param classifierName  String classifier name
     * @param params          HashMap hashed params for classifier
     * @param headers         HTTP REST call headers
     * @param ui              UriInfo
     * @param securityContext security context
     * @return Task URI
     * @throws NotFoundException file not found
     * @throws IOException       io exception
     */
    @Produces("text/plain")
    public Response algorithmPost(InputStream fileInputStream, String datasetUri,
                                  String classifierName, HashMap params, String metaClassifierName, HashMap metaParams,
                                  String validation, Double validationNum,
                                  HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException {
        return algorithmPost(fileInputStream, datasetUri, classifierName, params, metaClassifierName, metaParams, null,
                             validation, validationNum,headers, ui, securityContext);
    }

    /**
     * Train a classifier or meta classifier
     *
     * @param fileInputStream    dataset file handle
     * @param datasetUri         dataset URI
     * @param classifierName     String classifier name
     * @param params             HashMap hashed params for classifier
     * @param metaClassifierName Sting optional meta classifier name
     * @param metaParams         HashMap optional hashed params for meta classifier
     * @param paramString        option-string as used in WEKA for generic endpoint
     * @param headers            HTTP REST call headers
     * @param ui                 UriInfo
     * @param securityContext    security context
     * @return Task URI
     * @throws NotFoundException file not found
     * @throws IOException       io exception
     */
    @Produces({TEXT_URILIST, MediaType.APPLICATION_JSON})
    public Response algorithmPost(InputStream fileInputStream, String datasetUri,
                                  String classifierName, HashMap params, String metaClassifierName, HashMap metaParams,
                                  String paramString, String validation, Double validationNum,
                                  HttpHeaders headers, UriInfo ui, SecurityContext securityContext)
        throws NotFoundException, IOException {

        String subjectid = headers.getRequestHeaders().getFirst("subjectid");
        String txtStr = DatasetService.getArff(fileInputStream, datasetUri, subjectid);
        String baseuri = ui.getBaseUri().toString();
        String accept = headers.getRequestHeaders().getFirst("accept");
        String metaStr = (metaClassifierName != null ? metaClassifierName + " with " : "");

        TaskHandler task = new TaskHandler(classifierName, metaStr + classifierName + " algorithm", "Training data on " + metaStr + classifierName + " algorithm.", baseuri) {
            AbstractClassifier classifier;
            Instances trainingset = null;
            Instances testset = null;
            String validation = "";
            SingleClassifierEnhancer metaClassifier = null;
            String[] options = null;
            String validationMethod = "CrossValidation";

            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);  //wait until task is saved in mongoDB
                    setState(Task.Step.PREPARATION, 10f);
                    if (params != null) {
                        options = getClassifierOptions(classifierName, params);
                    } else {
                        options = WekaOptionHelper.splitOptions(paramString);
                    }

                    Instances instances = WekaUtils.instancesFromString(txtStr, true);
                    switch (validationMethod) {
                        case "HoldOut":
                            instances.randomize(new java.util.Random(0));
                            int trainSize = (int) Math.round(instances.numInstances() * 0.8);
                            int testSize = instances.numInstances() - trainSize;
                            trainingset = new Instances(instances, 0, trainSize);
                            testset = new Instances(instances, trainSize, testSize);
                            break;
                        default:
                            trainingset = instances;
                            break;
                    }
                    String id;
                    //Vector<Object> v = new Vector<>();
                    try {
                        classifier = getClassifier(classifierName);
                    } catch (Exception e) {
                        setErrorReport(e, 500, "algorithmPost: " + classifierName);
                        e.printStackTrace();
                    }

                    classifier.setOptions(options);

                    if (metaClassifierName != null) {
                        String[] metaOptions = getClassifierOptions(metaClassifierName, metaParams);
                        switch (metaClassifierName) {
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

                    if (metaClassifierName != null) {
                        metaClassifier.buildClassifier(trainingset);
                        setState(Task.Step.VALIDATION, 70f);
                        validation = Validation.crossValidation(trainingset, metaClassifier);
                        //v.add(metaClassifier);
                        //v.add(new Instances(instances, 0));
                        id = ModelService.saveModel(metaClassifier, ArrayUtils.addAll(metaClassifier.getOptions(), classifier.getOptions()), params, validation, subjectid);
                    } else {
                        classifier.buildClassifier(trainingset);
                        if (!Objects.equals(validationMethod, "HoldOut")) {
                            setState(Task.Step.VALIDATION, 70f);
                            validation = Validation.crossValidation(trainingset, classifier);
                        } else {
                            Evaluation eval = new Evaluation(trainingset);
                            eval.evaluateModel(classifier, testset);
                        }
                        //v.add(classifier);
                        //v.add(new Instances(instances, 0));
                        id = ModelService.saveModel(classifier, classifier.getOptions(), params, validation, subjectid);
                    }
                    String baseuri = ui.getBaseUri().toString();
                    setResultURI(baseuri + "model/" + id);
                    finish();
                } catch (Exception e) {
                    setErrorReport(e, 500, "algorithmPost: " + classifierName);
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

    AbstractClassifier getClassifier(String classifierName) {
        AbstractClassifier classifier = null;
        try {
            switch (classifierName) {
                case "BayesNet":
                    classifier = new BayesNet();
                    break;
                case "DecisionStump":
                    classifier = new DecisionStump();
                    break;
                case "GaussianProcesses":
                    classifier = new GaussianProcesses();
                    break;
                case "J48":
                    classifier = new J48();
                    break;
                case "IBk":
                    classifier = new IBk();
                    break;
                case "LibSVM":
                    classifier = new LibSVM();
                    break;
                case "LinearRegression":
                    classifier = new LinearRegression();
                    break;
                case "Logistic":
                    classifier = new Logistic();
                    break;
                case "M5P":
                    classifier = new M5P();
                    break;
                case "M5Rules":
                    classifier = new M5Rules();
                    break;
                case "MultilayerPerceptron":
                    classifier = new MultilayerPerceptron();
                    break;
                case "NaiveBayes":
                    classifier = new NaiveBayes();
                    break;
                case "RandomForest":
                    classifier = new RandomForest();
                    break;
                case "SMO":
                    classifier = new SMO();
                    break;
                case "SMOreg":
                    classifier = new SMOreg();
                    break;
                case "ZeroR":
                    classifier = new ZeroR();
                    break;
                // for generic interface
                case "AdaBoost":
                    classifier = new AdaBoostM1();
                    break;
                case "Bagging":
                    classifier = new Bagging();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            //setErrorReport(e,500, "algorithmPost: " +  classifierName);

        }
        return classifier;
    }

}
