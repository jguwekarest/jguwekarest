package io.swagger.api.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.api.ApiException;
import org.bson.Document;
import weka.classifiers.Classifier;

import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.Map;

public class ModelService {


    public static String listModels(UriInfo ui, String accept, String token) {
        Dao modelDao = new Dao();
        String modellist = modelDao.listData("model", ui, accept);
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
            out += "\n" + model.meta.get("className") + " " + model.meta.get("options").toString();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            modelDao.close();
        }
        return out;
    }

    public static String saveModel(Classifier classifier,String[] options, String validation, String token) {
        Dao modelDao = new Dao();
        String id = "";
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
            modelDao.saveData("model", document);
            id = "1";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            modelDao.close();
        }
        return id;
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
