package io.swagger.api.data;

import com.google.gson.Gson;
import io.swagger.api.ApiException;
import org.bson.Document;

import javax.ws.rs.core.UriInfo;
import java.io.*;

public class ModelService {


    public static String listModels(UriInfo ui, String accept, String token) {
        Dao modelDao = new Dao();
        String modellist = modelDao.listData("model", ui, accept);
        modelDao.close();
        return modellist;
    }

    public static String getModel(String id) throws ApiException {
        Dao modelDao = new Dao();
        Model model = modelDao.getModel(id);
        modelDao.close();
        String out = "";
        try {
            out = ModelService.deserialize(model.model).toString();
            out += "\n" + model.validation;
            out += "\n" + model.info;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }


    public static String saveModel(Model model, String token) {
        Dao datasetDao = new Dao();
        String id = "";
        try {
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(model));
            datasetDao.saveData("model", document);
            id = "1";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            datasetDao.close();
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
