package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.LazyService;
import io.swagger.api.data.DatasetService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class LazyImpl extends LazyService {
    @Override
    @Produces("text/plain")
    public Response algorithmKNNclassificationPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer windowSize, Integer KNN, Integer crossValidate, String distanceWeighting, Integer meanSquared, String nearestNeighbourSearchAlgorithm, String subjectid, SecurityContext securityContext) throws NotFoundException, IOException {
        // do some magic!
        Object[] params = {windowSize, KNN, crossValidate, distanceWeighting, meanSquared, nearestNeighbourSearchAlgorithm, subjectid};

        for (int i= 0; i < params.length; i ++  ) {
            System.out.println("kNN param " + i + " are: " + params[i]);
        }

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        String parameters = "";

        parameters += ((windowSize != null) ? (" -W " + windowSize + " ") : (" -W 0 ") );

        if (KNN != null && KNN != 1) {
            parameters += " -K " + KNN + " ";
        } else {
            parameters += " -K 1 ";
        }

        parameters += ((crossValidate != null && crossValidate != 0) ? " -X " : "");

        if (distanceWeighting != null) {
            if (distanceWeighting.equals("F") || distanceWeighting.equals("I")) {
                parameters += " -" + distanceWeighting + " ";
            }
        }

        if (meanSquared != null && meanSquared != 0) parameters += " -E ";

        //use LinearNNSearch fixed
        parameters += " -A ";
        parameters += "\"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";

        System.out.println("parameterstring for weka: IBk " + parameters.replaceAll("( )+", " "));

        IBk classifier = new IBk();

        Instances instances = WekaUtils.instancesFromString(txtStr);

        try {
            classifier.setOptions( weka.core.Utils.splitOptions(parameters.replaceAll("( )+", " ")) );
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.lazy.IBk\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = Validation.crossValidation(instances, classifier);

        Vector<Object> v = new Vector<>();
        v.add(classifier);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();

        //return Response.ok(MediaType.APPLICATION_JSON).entity(new ApiResponseMessage(ApiResponseMessage.OK, "Here it is magic!")).build();
    }

}
