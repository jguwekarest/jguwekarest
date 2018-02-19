package io.swagger.api.impl;

import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.FunctionsService;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
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

import static io.swagger.api.WekaOptionHelper.getLibSVMOptions;
import static io.swagger.api.impl.Validation.crossValidation;

public class FunctionsImpl extends FunctionsService {
    @Override
    @Produces("text/plain")

    public Response libSVMPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer svmType,
                               Float coef0, Float cost, Integer degree, BigDecimal eps, BigDecimal gamma, Integer kernelType,
                               BigDecimal loss, Boolean normalize, BigDecimal nu, Boolean probabilityEstimates, Boolean shrinking,
                               String weights, String subjectid, HttpHeaders headers, UriInfo ui, SecurityContext securityContext)
            throws IOException {

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        LibSVM classifier = new LibSVM();

        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        String[] options = getLibSVMOptions(svmType, coef0, cost, degree, eps, gamma, kernelType, loss, normalize, nu, probabilityEstimates, shrinking, weights);

        try {
            classifier.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.functions.LibSVM\n parameters: \"" + options.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.functions.LibSVM\n parameters: \"" + options.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(classifier, classifier.getOptions(), validation, subjectid);
            String baseuri = ui.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }
    }


    public Response linearRegressionPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                         Integer attributeSelectionMethod, Integer eliminateColinearAttributes, BigDecimal ridge,
                                         String subjectid, HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws IOException {

        Object[] params = {datasetUri,  attributeSelectionMethod, eliminateColinearAttributes, ridge, subjectid};

        for (int i= 0; i < params.length; i ++  ) {
            System.out.println("LR param " + i + " are: " + params[i]);
        }

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        String parameters = "";

        parameters += ((attributeSelectionMethod != null) ? (" -S " + attributeSelectionMethod + " ") : (" -S 1 ") );

        if (eliminateColinearAttributes != null && eliminateColinearAttributes == 0) parameters += " -C ";

        if (ridge != null ) parameters += " -R " + ridge + " ";

        parameters += " -num-decimal-places 4 ";

        System.out.println("parameterstring for weka: linearRegression " + parameters.replaceAll("( )+", " "));

        LinearRegression classifier = new LinearRegression();

        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        String[] options;

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            classifier.setOptions(options);
            // ? classifier.setOptions( weka.core.Utils.splitOptions(parameters.replaceAll("( )+", " ")) );
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.functions.LinearRegression\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.functions.LinearRegression\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(classifier, classifier.getOptions(), validation, subjectid);
            String baseuri = ui.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }

    }


}
