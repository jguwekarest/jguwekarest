package io.swagger.api.impl;

import io.swagger.api.algorithm.RulesService;
import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.data.DatasetService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static io.swagger.api.impl.Validation.crossValidation;

public class RulesImpl extends RulesService {

    @Override
    @Produces("text/plain")
    public Response algorithmZeroRPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, String subjectid, SecurityContext securityContext) throws NotFoundException, IOException {

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri);

        ZeroR zeror = new ZeroR();
        String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr);

        try {
            zeror.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.ZeroR\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, zeror);

        Vector<Object> v = new Vector<>();
        v.add(zeror);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + validation ).build();

    }

    @Produces("text/plain")
    public Response algorithmM5RulesPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                         Integer unpruned, Integer useUnsmoothed, Double minNumInstances, Integer buildRegressionTree,
                                         String subjectid, SecurityContext securityContext) throws NotFoundException, IOException {

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri);

        String parameters = "";

        // set unpruned
        if (unpruned == 1) { parameters += " -N ";}

        // set use unsmoothed
        if (useUnsmoothed == 1) { parameters += " -U ";}

        // Set minNumInstances
        if (minNumInstances != null) {
            parameters += " -M " + minNumInstances + " ";
        } else {
            parameters += " -M 4.0 ";
        }

        // set buildRegressionTree
        if (buildRegressionTree == 1) { parameters += " -R ";}

        System.out.println("parameterstring for weka: M5Rules " + parameters);

        M5Rules classifier = new M5Rules();
        String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr);

        try {
            classifier.setOptions(weka.core.Utils.splitOptions(parameters));
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.M5Rules\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + validation ).build();

    }
}
