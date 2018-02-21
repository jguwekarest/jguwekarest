package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaOptionHelper;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.LazyService;
import io.swagger.api.data.DatasetService;
import io.swagger.api.data.ModelService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

public class LazyImpl extends LazyService {
    @Override
    @Produces("text/plain")
    public Response algorithmKNNclassificationPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                                   Integer windowSize, Integer KNN, Integer crossValidate, String distanceWeighting, Integer meanSquared,
                                                   String nearestNeighbourSearchAlgorithm, String subjectid, HttpHeaders headers, UriInfo uriInfo)
            throws NotFoundException, IOException {

        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("datasetUri", datasetUri);
        params.put("windowSize", windowSize);
        params.put("KNN", KNN);
        params.put("crossValidate", crossValidate);
        params.put("distanceWeighting", distanceWeighting);
        params.put("meanSquared", meanSquared);
        params.put("nearestNeighbourSearchAlgorithm", nearestNeighbourSearchAlgorithm);

        String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);

        String[] options = WekaOptionHelper.getKNNOptions(windowSize, KNN, crossValidate, distanceWeighting, meanSquared, nearestNeighbourSearchAlgorithm);

        IBk classifier = new IBk();

        Instances instances = WekaUtils.instancesFromString(txtStr, true);

        try {
            classifier.setOptions( options );
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.lazy.IBk\n parameters: \"" + options.toString() + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = Validation.crossValidation(instances, classifier);

        if(accept.equals("text/uri-list")) {
            String id = ModelService.saveModel(classifier, classifier.getOptions(), params, validation, subjectid);
            String baseuri = uriInfo.getBaseUri().toString();
            return Response.ok(baseuri + "model/" + id).build();
        } else {
            Vector<Object> v = new Vector<>();
            v.add(classifier);
            v.add(new Instances(instances, 0));
            return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
        }

    }

}
