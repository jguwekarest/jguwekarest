package io.swagger.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.api.ApiException;
import io.swagger.api.StringUtil;
import io.swagger.api.WekaUtils;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.attribute.StringToNominal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatasetService {

    private static final Logger LOG = Logger.getLogger(DatasetService.class.getName());

    static String listDatasets(UriInfo ui, String accept, String token) {
        Dao datasetDao = new Dao();
        String dslist = datasetDao.listData("dataset", ui, accept);
        datasetDao.close();
        //System.out.println(toArffWeka("test"));
        return dslist;
    }

    static String getDatasetArff(String id, String token){
        String arff = "";
        Dao datasetDao = new Dao();
        try {
            arff = datasetDao.getDatasetArff(id);
        }finally {
            datasetDao.close();
        }
        return arff;
    }

    public static String getArff(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetURI, String subjectid) throws IOException {
        StringBuilder txtStr = new StringBuilder();
        if (datasetURI != null && !Objects.equals(datasetURI, "")) {
            if(StringUtil.isUri(datasetURI)) {
                System.out.println("isUri true: " + datasetURI);
            } else {
                txtStr.append(DatasetService.getDatasetArff(datasetURI, subjectid));
            }
        } else {
            int c;
            while ((c = fileInputStream.read()) != -1) {
                txtStr.append((char) c);
            }
        }
        return txtStr.toString();
    }

    /**
     * Download an external dataset in JSON format (e.G.: from JAQPOT service)
     * @param uri URI of the external dataset
     * @param token authentication token
     * @return representation of the dataset in Dataset class
    */

    static Dataset readExternalDataset(String uri, String token) throws ApiException {
        String jsonString = "";
        Client client = ClientBuilder.newClient();
        // MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

        Response response = client.target(uri)
                .request()
                .header("subjectid", token)
                .accept(MediaType.APPLICATION_JSON)
                .get();

        String responseValue = response.readEntity(String.class);

        response.close();
        jsonString = responseValue;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("MM-dd-yyyy HH:mm"); // setting custom date format
        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonString, Dataset.class);
    }

    /**
     * Converts a Dataset to arff.
     * @param dataset a Dataset object
     * @return an arff String
     */
    static String toArff(Dataset dataset, String class_uri) {

        StringBuilder arff = new StringBuilder();
        StringBuilder comment = new StringBuilder();

        //add comments datasetURI and dataset metadata
        comment.append("% JGU weka service converted dataset from :").append(dataset.datasetURI).append("\n%\n");
        comment.append("% Using ").append(class_uri != null ? ("feature " + class_uri) : "no feature").append(" for the weka class.\n%\n");
        if (dataset.meta != null) {
            Set metaEntries = dataset.meta.entrySet();
            for (Object metaEntry : metaEntries) {
                Map.Entry me = (Map.Entry) metaEntry;
                comment.append("% meta ").append(me.getKey()).append(": ").append(me.getValue().toString().replaceAll("^[\\[]|[\\]]$", "")).append("\n");
            }
        }

        for (int i = 0; i < dataset.features.size(); i++) {
            Dataset.Feature feat = dataset.features.get(i);
            if (feat.uri != null) comment.append("% feature:     uri: ").append(feat.uri).append("\n");
            if (feat.name != null) comment.append("%             name: ").append(feat.name).append("\n");
            if (feat.units != null) comment.append("%            units: ").append(feat.units).append("\n");
            if (feat.category != null) comment.append("%         category: ").append(feat.category).append("\n");
            if (feat.conditions != null) comment.append("%       conditions: ").append(feat.conditions).append("\n%\n");
        }

        //add current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        comment.append("% created: ").append(sdf.format(new Date())).append("\n\n");
        arff.append("@relation ").append(dataset.datasetURI).append("\n");

        StringBuilder dataStr = new StringBuilder("\n@data\n");

        //check if features are not numeric
        Map<String,String> featureIsNumeric = new HashMap<>();
        for (int i = 0; i < dataset.features.size(); i++) {
            featureIsNumeric.put(dataset.features.get(i).uri, "Numeric");
        }
        List<String> classValues =  new ArrayList<>();
        for (int j = 0; j < dataset.dataEntry.size(); j++) {
            StringBuilder line = new StringBuilder();
            String classVal = "";
            Dataset.DataEntry de = dataset.dataEntry.get(j);

            line.append("'").append(de.compound.get("URI")).append("'");
            for (int i = 0; i < dataset.features.size(); i++) {
                String val = de.values.get(dataset.features.get(i).uri);
                if (!StringUtil.isNumeric(val) && !Objects.equals(val, "null")) {
                    featureIsNumeric.put(dataset.features.get(i).uri, "String");
                }
                if (val == null) val = "?";

                if (class_uri != null && dataset.features.get(i).uri.equals(class_uri)){
                    classVal = "," +  val;
                    classValues.add(val);
                } else {
                    line.append(",").append(val);
                }
            }
            dataStr.append(line).append(classVal).append("\n");
        }

        Set<String> classValuesUnique = new LinkedHashSet<>(classValues);

        arff.append("@attribute URI String\n");
        String classAttr = "";
        for (int i = 0; i < dataset.features.size(); i++) {
            if (class_uri != null && dataset.features.get(i).uri.equals(class_uri)) {
                String attributeValues = "";
                if (classValuesUnique.size() > 0) {
                    attributeValues = " { " + String.join(", ", classValuesUnique) + " }";
                }
                classAttr = "@attribute " + dataset.features.get(i).uri + attributeValues;
            } else {
                arff.append("@attribute ").append(dataset.features.get(i).uri).append(" ").append(featureIsNumeric.get(dataset.features.get(i).uri)).append("\n");
            }
        }
        arff.append(classAttr);
        arff.append(dataStr);
        Dao datasetDao = new Dao();
        try {
            dataset.arff = arff.toString();
            dataset.comment = comment.toString();
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(dataset));
            datasetDao.saveData("dataset", document);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            datasetDao.close();
        }
        return comment.toString() + "\n" + arff.toString();
    }

    static String filter(Dataset dataset, String idx_remove, String scale, String translation, Boolean standardize, Boolean ignore, String attributeRange) throws Exception {
        String out = dataset.arff;
        if(idx_remove != null && !idx_remove.equals("0")){
            LOG.log(Level.INFO, "Remove filter: attributes: {0}", idx_remove);
            Remove rm = new Remove();
            String[] options = new String[]{"-R",idx_remove};
            rm.setOptions(options);
            Instances instances = WekaUtils.instancesFromString(out, true);
            rm.setInputFormat(instances);
            Instances newData = Filter.useFilter(instances,rm);
            out = newData.toString();
        }
        if (ignore == null) ignore = false;
        if(scale != null && translation != null){
            LOG.log(Level.INFO, "Normalize filter: scale: {0}, translation: {1}, ignore class: {2}", new Object[]{scale,translation, ignore});
            Normalize norm = new Normalize();
            if(ignore) norm.setIgnoreClass(true);
            String[] options = { "-S", scale, "-T", translation };
            norm.setOptions(options);
            Instances instances = WekaUtils.instancesFromString(out, true);
            norm.setInputFormat(instances);
            Instances newData = Filter.useFilter(instances, norm);
            out = newData.toString();
        } else if (standardize != null && standardize){
            LOG.log(Level.INFO, "Standardize filter: ignore class: {0}",ignore);
            Standardize stand = new Standardize();
            if(ignore) stand.setIgnoreClass(true);
            Instances instances = WekaUtils.instancesFromString(out, true);
            stand.setInputFormat(instances);
            Instances newData = Filter.useFilter(instances, stand);
            out = newData.toString();
        }
        // String to Nominal
        if(attributeRange != null) {
            StringToNominal s2n = new StringToNominal();
            s2n.setAttributeRange(attributeRange);
            Instances instances = WekaUtils.instancesFromString(out, true);
            s2n.setInputFormat(instances);
            Instances newData = new Instances(StringToNominal.useFilter(instances, s2n));
            out = newData.toString();
        }
        return out;
    }

    static String toArffWeka(String bla) {
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
        ArrayList<Attribute> attVals = new ArrayList<Attribute>();
        // - numeric
        atts.add(new Attribute("att1"));
        atts.add(new Attribute("att2"));
        // 2. create Instances object
        Instances data = new Instances("MyRelation", atts, 0);

        double[] vals = new double[data.numAttributes()];

        // - numeric
        vals[0] = Math.PI;
        // - nominal
        vals[1] = attVals.indexOf("val3");

        System.out.println(data);
        return "";
    }

}
