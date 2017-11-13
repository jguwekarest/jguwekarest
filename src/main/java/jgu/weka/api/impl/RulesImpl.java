package jgu.weka.api.impl;

import jgu.weka.api.NotFoundException;
import jgu.weka.api.WekaUtils;
import jgu.weka.api.algorithm.RulesService;
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

import static jgu.weka.api.impl.Validation.crossValidation;

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

        String validation = "";
        validation = crossValidation(instances, zeror);

        Vector v = new Vector();
        v.add(zeror);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + validation ).build();

    }

    @Override
    @Produces("text/plain")
    public Response algorithmM5RulesPost(InputStream fileInputStream, FormDataContentDisposition fileDetail,
                                         Integer unpruned, Integer useUnsmoothed, Double minNumInstances, Integer buildRegressionTree,
                                         String subjectid, SecurityContext securityContext) throws NotFoundException, IOException {

        StringBuffer txtStr = new StringBuffer();
        int c;
        while ((c = fileInputStream.read()) != -1) {
            txtStr.append((char)c);
        }

        StringBuilder parameters = new StringBuilder();

        // set unpruned
        if (unpruned == 1) { parameters.append(" -N ");}

        // set use unsmoothed
        if (useUnsmoothed == 1) { parameters.append(" -U ");}

        // Set minNumInstances
        if (minNumInstances != null) {
            parameters.append(" -M " + minNumInstances + " ");
        } else {
            parameters.append(" -M 4.0 ");
        }

        // set buildRegressionTree
        if (buildRegressionTree == 1) { parameters.append(" -R ");}

        System.out.println("parameterstring for weka: M5Rules " + parameters.toString());



        M5Rules m5rules = new M5Rules();
        String[] options = new String[0];

        Instances instances = WekaUtils.instancesFromString(txtStr.toString());

        try {
            m5rules.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error: WEKA weka.classifiers.rules.M5Rules\nWeka error message: " + e.getMessage() + "\n").build();
        }

        String validation = "";
        validation = crossValidation(instances, m5rules);

        Vector v = new Vector();
        v.add(m5rules);
        v.add(new Instances(instances, 0));

        return Response.ok(v.toString() + "\n" + validation ).build();

    }
}
