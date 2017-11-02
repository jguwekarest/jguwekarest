package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.LazyService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static io.swagger.api.impl.Validation.crossValidation;

public class LazyImpl extends LazyService {
    @Override
    @Produces("text/plain")
    public Response algorithmKNNclassificationPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, Integer windowSize, Integer KNN, Integer crossValidate, String distanceWeighting, Integer meanSquared, String nearestNeighbourSearchAlgorithm, String subjectid, SecurityContext securityContext) throws NotFoundException, IOException {
        // do some magic!
        Object[] params = {windowSize, KNN, crossValidate, distanceWeighting, meanSquared, nearestNeighbourSearchAlgorithm, subjectid};

        for (int i= 0; i < params.length; i ++  ) {
            System.out.println("kNN param " + i + " are: " + params[i]);
        }

        StringBuffer txtStr = new StringBuffer();
        int c;
        while ((c = fileInputStream.read()) != -1) {
            txtStr.append((char)c);
        }

        StringBuilder parameters = new StringBuilder();

        parameters.append((windowSize != null) ? (" -W " + windowSize + " ") : (" -W 0 ") );

        if (KNN != null && KNN != 1) {
            parameters.append(" -K " + KNN + " ");
        } else {
            parameters.append(" -K 1 ");
        }

        parameters.append((crossValidate != null && crossValidate != 0) ? " -X " : "");

        if (distanceWeighting != null) {
            if (distanceWeighting == "F" || distanceWeighting == "I") {
                parameters.append(" -" + distanceWeighting + " ");
            }
        }

        if (meanSquared != null && meanSquared != 0) parameters.append(" -E ");

        //use LinearNNSearch fixed
        parameters.append(" -A ");
        parameters.append("\"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"");

        System.out.println("parameterstring for weka: IBk " + parameters.toString().replaceAll("( )+", " "));

        IBk kNN = new IBk();

        Instances instances = WekaUtils.instancesFromString(txtStr.toString());

        try {
            kNN.setOptions( weka.core.Utils.splitOptions(parameters.toString().replaceAll("( )+", " ")) );
            kNN.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.lazy.IBk\n parameters: \"" + parameters.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, kNN);

        Vector v = new Vector();
        v.add(kNN);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();

        //return Response.ok(MediaType.APPLICATION_JSON).entity(new ApiResponseMessage(ApiResponseMessage.OK, "Here it is magic!")).build();
    }

}
