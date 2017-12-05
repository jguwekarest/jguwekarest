package io.swagger.api.impl;

import io.swagger.api.algorithm.FunctionsService;
import io.swagger.api.WekaUtils;
import io.swagger.api.data.DatasetService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Vector;

import static io.swagger.api.impl.Validation.crossValidation;

public class FunctionsImpl extends FunctionsService {
    @Override
    @Produces("text/plain")

    public Response linearRegressionPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer attributeSelectionMethod, Integer eliminateColinearAttributes, BigDecimal ridge, String subjectid, SecurityContext securityContext) throws IOException {

        Object[] params = {/* predictionFeature, datasetUri, datasetService, */  attributeSelectionMethod, eliminateColinearAttributes, ridge, subjectid};

        for (int i= 0; i < params.length; i ++  ) {
            System.out.println("LR param " + i + " are: " + params[i]);
        }

        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri);

        String parameters = "";

        parameters += ((attributeSelectionMethod != null) ? (" -S " + attributeSelectionMethod + " ") : (" -S 1 ") );

        if (eliminateColinearAttributes != null && eliminateColinearAttributes == 0) parameters += " -C ";

        if (ridge != null ) parameters += " -R " + ridge + " ";

        parameters += " -num-decimal-places 4 ";

        System.out.println("parameterstring for weka: linearRegression " + parameters.replaceAll("( )+", " "));
        LinearRegression LR = new LinearRegression();

        Instances instances = WekaUtils.instancesFromString(txtStr);

        try {
            LR.setOptions( weka.core.Utils.splitOptions(parameters.replaceAll("( )+", " ")) );
            LR.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: check options for WEKA weka.classifiers.functions.LinearRegression\n parameters: \"" + parameters + "\"\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, LR);

        Vector v = new Vector();
        v.add(LR);
        v.add(new Instances(instances, 0));


        return Response.ok(v.toString() + "\n" + validation + "\n", "text/x-arff").build();
    }


}
