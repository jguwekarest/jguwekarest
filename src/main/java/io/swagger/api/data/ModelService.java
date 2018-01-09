package io.swagger.api.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.api.ApiException;
import io.swagger.api.WekaUtils;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.Map;

public class ModelService {

    private static String dataDirectory = System.getProperty("user.home") + "/.jguweka/data/";

    public static Object listModels(UriInfo ui, String accept, String token) {
        Dao modelDao = new Dao();
        Object modellist = modelDao.listData("model", ui, accept);
        modelDao.close();
        return modellist;
    }

    public static String getModel(String id) throws ApiException {
        String out = "";
        Dao modelDao = new Dao();
        try {
            Model model = modelDao.getModel(id);
            out = ModelService.deserialize(model.model).toString();
            out += "\n" + model.validation;
            out += "\nModel build options:";
            out += "\n" + model.meta.get("className") + " " + model.meta.get("options");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            modelDao.close();
        }
        return out;
    }

    public static String saveModel(Classifier classifier, String[] options, String validation, String token) {
        Dao modelDao = new Dao();
        String id;
        try {
            Model model = new Model();
            model.model = ModelService.serialize(classifier);

            System.out.println("Class is: " + classifier.getClass().getName());
            Map<String, String> map = Maps.newHashMap();

            map.put("options", String.join(" ", options));
            map.put("className", classifier.getClass().getName());
            model.setMeta(map);
            model.validation = validation;

            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(model));
            id = modelDao.saveData("model", document);
            weka.core.SerializationHelper.write(dataDirectory + id + ".model", classifier);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            modelDao.close();
        }
        return id;
    }

    /**
    * Predict a dataset in arff format with an existing local model.
    * @param fileInputStream file handle to upload an arff file
    * @param fileDetail file handle to upload an arff file
    * @param datasetId ID of a local dataset
    * @param modelId ID of the local model in mongoDB
    * @param subjectid token for authentication and authorization
    *
    */
    public static String predictModel(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetId, String modelId, String subjectid) throws Exception {
        StringBuilder out = new StringBuilder();
        Classifier cls = getClassifier(modelId);
        String arff = DatasetService.getArff(fileInputStream, fileDetail, datasetId, subjectid);
        Instances instances = WekaUtils.instancesFromString(arff, true);
        for (Instance instance: instances) {
            Double result = cls.classifyInstance(instance);
            out.append(instance).append(" :: ").append(result).append("\n");
        }
        return out.toString();
    }

    /**
    * Get a WEKA classifier from mongodb
    * @param id ID of the model
    * @return weka.classifiers.Classifier
    */
    public static Classifier getClassifier(String id) throws Exception {
        Model model = null;
        Dao modelDao = new Dao();
        try {
            model = modelDao.getModel(id);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            modelDao.close();
        }
        Classifier cls;
        ByteArrayInputStream in = new ByteArrayInputStream(model.model);
        ObjectInputStream ois = new ObjectInputStream(in);
        cls = (Classifier) ois.readObject();
        ois.close();
        return cls;
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

}
