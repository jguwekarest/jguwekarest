package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.TreesService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.Vector;

import static io.swagger.api.impl.Validation.crossValidation;

public class TreesImpl extends TreesService {

    @Override
    @Produces("text/plain")
    public Response algorithmJ48Post(InputStream fileInputStream, FormDataContentDisposition fileDetail, Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds, Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned, Integer useLaplace, SecurityContext securityContext, ServletContext servletContext) throws NotFoundException, IOException {

        Object[] params = {binarySplits, confidenceFactor, minNumObj, numFolds, reducedErrorPruning, seed, subtreeRaising, unpruned, useLaplace};

        for (int i = 0; i < params.length; i++) {
            System.out.println("param are: " + params[i]);
        }

        StringBuffer txtStr = new StringBuffer();
        int c;
        while ((c = fileInputStream.read()) != -1) {
            txtStr.append((char) c);
        }

        StringBuilder parameters = new StringBuilder();

        if (binarySplits == 1) {
            parameters.append(" -B ");
        }

        if (minNumObj != null) {
            parameters.append(" -M " + minNumObj);
        } else {
            parameters.append(" -M 2 ");
        }

        if (reducedErrorPruning == 1) {
            if (numFolds != null) {
                parameters.append(" -R -N " + numFolds);
            } else {
                parameters.append(" -R -N 3 ");
            }
        }

        if (seed != null) {
            parameters.append(" -Q " + seed);
        } else {
            parameters.append(" -Q 2 ");
        }

        if (unpruned == 1) {
            parameters.append(" -U ");
        } else {
            if (subtreeRaising == 0) {
                parameters.append(" -S ");
            }
            if (confidenceFactor != null) {
                parameters.append(" -C " + confidenceFactor);
            } else {
                parameters.append(" -C 0.25 ");
            }
        }

        if (useLaplace !=null && useLaplace == 1) {
            parameters.append(" -A ");
        }

        System.out.println("parameterstring for weka: weka.classifiers.trees.J48 " + parameters.toString());

        J48 j48 = new J48();
        String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr.toString());

        try {
            options = weka.core.Utils.splitOptions(parameters.toString());
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        try {
            j48.setOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.trees.j48\n parameters: \"" + parameters.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }
        try {
            j48.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.trees.j48\n parameters: \"" + parameters.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = Validation.crossValidation(instances, j48);

        Vector v = new Vector();
        v.add(j48);
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
