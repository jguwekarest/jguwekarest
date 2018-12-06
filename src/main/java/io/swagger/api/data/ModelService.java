package io.swagger.api.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.api.ApiException;
import io.swagger.api.WekaUtils;
import org.bson.Document;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("ALL")
public class ModelService {

    private static String dataDirectory = System.getProperty("user.home") + "/.jguweka/data/";
    private static final Logger LOG = Logger.getLogger(ModelService.class.getName());


    /**
     * Get a list of models from mongoDB.
     * @param ui UriInfo
     * @param accept requested mime-type
     * @param token security token
     * @return list of models
     */
    public static Object listModels(UriInfo ui, String accept, String token) {
        Dao modelDao = new Dao();
        Object modellist = modelDao.listData("model", ui, accept);
        modelDao.close();
        return modellist;
    }

    /**
     * Get a model from mongoDB.
     * @param id dataset/mongodb id
     * @return model as string
     */
    public static Object getModel(String id, String accept) throws ApiException {
        Object out = "";
        Dao modelDao = new Dao();
        try {
            Model model = modelDao.getModel(id);

            if (accept.equals("text/plain")) {
                out = ModelService.deserialize(model.model).toString();
                out += "\n" + model.validation;
                out += "\nModel build options:";
                out += "\n" + model.meta.get("className") + " " + model.meta.get("options");
            } else {
                out = model;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            modelDao.close();
        }
        return out;
    }

    /**
     * Save a model
     * @param classifier classifier
     * @param options build options
     * @param validation validation
     * @param token security token
     * @return model id
     */
    public static String saveModel(Classifier classifier, String[] options, String validation, @SuppressWarnings("unused") String token) {
        Dao modelDao = new Dao();
        String id;
        try {
            Model model = new Model();
            model.model = ModelService.serialize(classifier);

            System.out.println("Class is: " + classifier.getClass().getName());
            Map<String, Object> map = Maps.newHashMap();

            map.put("options", String.join(" ", options));
            map.put("className", classifier.getClass().getName());
            model.setMeta(map);
            model.validation = validation;

            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(model));
            id = modelDao.saveData("model", document);
            //weka.core.SerializationHelper.write(dataDirectory + id + ".model", classifier);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            modelDao.close();
        }
        return id;
    }
    /**
     * Save a model
     * @param classifier classifier
     * @param options build options
     * @param params build parameter
     * @param validation validation
     * @param token security token
     * @return model id
     */
    public static String saveModel(Classifier classifier, String[] options, Map params, String validation, String token) throws Exception {
        Dao modelDao = new Dao();
        String id;
        try {
            Model model = new Model();
            model.model = ModelService.serialize(classifier);

            System.out.println("Class is: " + classifier.getClass().getName());
            Map<String, Object> map = Maps.newHashMap();

            map.put("options", String.join(" ", options));
            map.put("className", classifier.getClass().getName());
            //map.put("buildParams", params);
            model.setMeta(map);
            model.validation = validation;
            model.meta.put("buildParams", params);

            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(model));
            id = modelDao.saveData("model", document);
            //weka.core.SerializationHelper.write(dataDirectory + id + ".model", classifier);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error in ModelService.saveModel " + e.getCause() + ": " + e.getMessage());
        } finally {
            modelDao.close();
        }
        return id;
    }

    /**
     * Delete a model.
     * @param id of the model
     * @return true on success
     * @throws ApiException error message
     */
    public static Boolean deleteModel(String id) throws ApiException {
        Dao dao = new Dao();
        try {
            Boolean status = dao.delete("model", id);
            LOG.log(Level.INFO, "Model : " + id + " deleted.");
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dao.close();
        }
    }


    /**
    * Predict a dataset in arff format with an existing local model.
    * @param fileInputStream file handle to upload an arff file
    * @param datasetId ID of a local dataset
    * @param modelId ID of the local model in mongoDB
    * @param subjectid token for authentication and authorization
    *
    */
    public static String predictModel(InputStream fileInputStream, String datasetId, String modelId, String subjectid) throws Exception {
        StringBuilder out = new StringBuilder();
        Classifier cls = getClassifier(modelId);
        String arff = DatasetService.getArff(fileInputStream, datasetId, subjectid);
        Instances instances = WekaUtils.instancesFromString(arff, true);
        for (Instance instance: instances) {
            Double result = cls.classifyInstance(instance);
            out.append(instance).append(",").append(result).append("\n");
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
