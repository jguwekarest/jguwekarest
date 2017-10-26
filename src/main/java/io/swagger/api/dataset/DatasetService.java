package io.swagger.api.dataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.api.ApiException;
import io.swagger.api.StringUtil;
import io.swagger.api.dao.Dao;
import org.bson.Document;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatasetService {

    public static Object cmp;

    public static String listDatasets(String token, UriInfo ui, String accept) {
        Dao datasetDao = new Dao();
        String dslist = datasetDao.getDatasetList(ui, accept);
        datasetDao.close();
        return dslist;
    }

    public static String getDatasetArff(String id, String token){
        Dao datasetDao = new Dao();
        String arff = datasetDao.getDatasetArff(id);
        datasetDao.close();
        return arff;
    }

    public static Dataset readExternalDataset(String uri, String token) throws ApiException {

        String jsonString = "";
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

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
        Dataset dataset = gson.fromJson(jsonString, Dataset.class);

        return dataset;
    }

    /**
     * Converts a Dataset to arff.
     *
     * @param dataset a Dataset object
     * @return an arff String
     */
    public static String toArff(Dataset dataset, String class_uri) {

        String arff = "";
        //add comments datasetURI and dataset metadata
        arff += "% JGU weka service converted dataset from :" + dataset.datasetURI + "\n%\n";
        arff += "% Using " + (class_uri != null ? ("feature " + class_uri): "no feature") + " for the weka class.\n%\n";
        if (dataset.meta != null) {
            Set metaEntries = dataset.meta.entrySet();
            Iterator metaIterator = metaEntries.iterator();
            while (metaIterator.hasNext()) {
                Map.Entry me = (Map.Entry) metaIterator.next();
                String val = me.getValue().toString();
                arff += "% meta " + me.getKey() + ": " + me.getValue().toString().replaceAll("^[\\[]|[\\]]$", "") + "\n";
            }
        }

        for (int i = 0; i < dataset.features.size(); i++) {
            arff += "% feature:     uri: " + dataset.features.get(i).uri + "\n";
            arff += "%             name: " + dataset.features.get(i).name + "\n";
            arff += "%            units: " + dataset.features.get(i).units + "\n";
            arff += "%         category: " + dataset.features.get(i).category + "\n";
            arff += "%       conditions: " + dataset.features.get(i).conditions + "\n%\n";
        }

        //add current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        arff += "% created: " + sdf.format(new Date()) + "\n\n";
        arff += "@relation " + dataset.datasetURI + "\n";

        String dataStr = "\n@data\n";

        //check if features are not numeric
        Map<String,String> featureIsNumeric = new HashMap<String,String>();
        for (int i = 0; i < dataset.features.size(); i++) {
            featureIsNumeric.put(dataset.features.get(i).uri, "Numeric");
        }
        List classValues =  new ArrayList<>();
        for (int j = 0; j < dataset.dataEntry.size(); j++) {
            String line = "", classVal = "";
            Dataset.DataEntry de = dataset.dataEntry.get(j);

            line += "'" + de.compound.get("URI") + "'";
            for (int i = 0; i < dataset.features.size(); i++) {
                String val = de.values.get(dataset.features.get(i).uri);
                if (!StringUtil.isNumeric(val) && val != "null") {
                    featureIsNumeric.put(dataset.features.get(i).uri, "String");
                }
                if (val == null) val = "?";

                if (class_uri != null && dataset.features.get(i).uri.equals(class_uri)){
                    classVal = "," +  val;
                    classValues.add(val);
                } else {
                    line += "," + val;
                }
            }
            dataStr += line + classVal + "\n";
        }

        Set<String> classValuesUnique = new LinkedHashSet<>(classValues);

        arff += "@attribute URI String\n";
        String classAttr = "";
        for (int i = 0; i < dataset.features.size(); i++) {
            if (class_uri != null && dataset.features.get(i).uri.equals(class_uri)) {
                String attributeValues = "";
                if (classValuesUnique.size() > 0) {
                    attributeValues = " { " + String.join(", ", classValuesUnique) + " }";
                }
                classAttr = "@attribute " + dataset.features.get(i).uri + attributeValues;
            } else {
                arff += "@attribute " + dataset.features.get(i).uri + " " + featureIsNumeric.get(dataset.features.get(i).uri) + "\n";
            }
        }
        arff += classAttr;
        arff += dataStr;
        Dao datasetDao = new Dao();
        try {
            dataset.arff = arff;
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(dataset));
            datasetDao.saveDataset(document);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            datasetDao.close();
        }
        return arff;
    }


}
