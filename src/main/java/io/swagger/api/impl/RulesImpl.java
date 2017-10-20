package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.WekaUtils;
import io.swagger.api.algorithm.RulesService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class RulesImpl extends RulesService {


    @Override
    @Produces("text/plain")
    public Response algorithmZeroRPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String subjectid, SecurityContext securityContext) throws NotFoundException, IOException {

        StringBuffer txtStr = new StringBuffer();
        int c;
        while ((c = fileInputStream.read()) != -1) {
            txtStr.append((char)c);
        }

        ZeroR zeror = new ZeroR();
        String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr.toString());

        try {
            zeror.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.ZeroR\nWeka error message: " + e.getMessage() + "\n").build();
        }
        String eval_out = "";
        try {
            Evaluation eval = new Evaluation(instances);
            eval.evaluateModel(zeror, instances);
            eval_out = eval.toSummaryString("\n=== Use training set Results ===\n", false);
            eval_out += "\n" + eval.toClassDetailsString() + "\n";
            eval_out += "\n" + eval.toMatrixString() + "\n";
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.ZeroR Evaluation Error:\nWeka error message: " + e.getMessage() + "\n").build();
        }

        Vector v = new Vector();
        v.add(zeror);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + eval_out ).build();

    }
}
