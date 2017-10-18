package io.swagger.api.dataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.api.ApiException;
import io.swagger.api.StringUtil;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatasetService {

    public static Object cmp;

    public static Dataset readDataset(String uri, String token) throws ApiException {

        String jsonString = "";
        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

        Response response = client.target(uri)
                .request()
                .header("subjectid", token)
                .accept(MediaType.APPLICATION_JSON)
                .get();

        String responseValue = response.readEntity(String.class);
        System.out.println(response.getHeaders());
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
    public static String toArff(Dataset dataset) {

        String arff = "";
        //add comments datasetURI and dataset metadata
        arff += "% JGU weka service converted dataset from " + dataset.datasetURI + "\n%\n";
        if (dataset.meta != null) {
            Set metaEntries = dataset.meta.entrySet();
            Iterator metaIterator = metaEntries.iterator();
            while (metaIterator.hasNext()) {
                Map.Entry me = (Map.Entry) metaIterator.next();
                String val = me.getValue().toString();
                arff += "% meta " + me.getKey() + ": " + me.getValue().toString().replaceAll("^[\\[]|[\\]]$", "") + "\n";
            }
        }
        //add current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        arff += "% created: " + sdf.format(new Date()) + "\n\n";

        arff += "@relation " + dataset.datasetURI + "\n";

        String dataStr = "";
        dataStr += "\n@data\n";
        //check if features are not numeric
        Map<String,String> featureIsNumeric = new HashMap<String,String>();
        for (int i = 0; i < dataset.features.size(); i++) {
            featureIsNumeric.put(dataset.features.get(i).uri, "Numeric");
        }

        for (int j = 0; j < dataset.dataEntry.size(); j++) {
            String line = "";
            Dataset.Entries de = dataset.dataEntry.get(j);
            Class compound = de.compound.getClass();
            line += "'" + de.compound.get("URI") + "'";
            for (int i = 0; i < dataset.features.size(); i++) {
                String val = de.values.get(dataset.features.get(i).uri);
                if (!StringUtil.isNumeric(val) && val != "null") {
                    featureIsNumeric.put(dataset.features.get(i).uri, "String");
                }
                if (val == null) val = "?";
                line += "," + val;
            }
            dataStr += line + "\n";
            Map values = de.values;
            if (values != null){
                System.out.println("entries are: " + line);
            }

        }
        arff += "@attribute URI String\n";
        for (int i = 0; i < dataset.features.size(); i++) {
            arff += "@attribute " + dataset.features.get(i).uri + " " + featureIsNumeric.get(dataset.features.get(i).uri) + "\n";
        }
        arff += dataStr;
        return arff;
    }


}
