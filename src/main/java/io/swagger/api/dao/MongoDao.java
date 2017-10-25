package io.swagger.api.dao;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.swagger.api.dataset.Dataset;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDao {

    private String dbName;
    private String dbHost;
    private Integer dbPort;
    private MongoClient mongoClient;
    private MongoDatabase mongoDB;
    private MongoCollection mongoCollection;

    private static Properties dbProperties = new Properties();
    private static final Logger LOG = Logger.getLogger(MongoDao.class.getName());

    public MongoDao() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream("config/db.properties");
        try {
            dbProperties.load(is);
            dbName = dbProperties.getProperty("db.name");
            dbHost = dbProperties.getProperty("db.host");
            dbPort = Integer.parseInt(dbProperties.getProperty("db.port"));
            LOG.log(Level.INFO, "Database configuration read!");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "No DB properties file found!", ex);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Database configuration can not be loaded!");
        } finally {
            mongoClient = new MongoClient(dbHost, dbPort);
            mongoDB = mongoClient.getDatabase(dbName);

            LOG.log(Level.INFO, "Database configured and connection established successfully!");
        }
    }

    public String getModelList() {
        final String result = "";
        int modelsCount = 0;
        mongoCollection = mongoDB.getCollection("model");
        try (MongoCursor<Document> cursor = mongoCollection.find().iterator()) {
            while(cursor.hasNext()) {
                Document document = cursor.next();
                result.concat(document.get("_id") + "\n");
                modelsCount++;
            }
            cursor.close();
        } finally {

        }
        LOG.log(Level.INFO, "Retrieved " + modelsCount + " models");
        return result;
    }


    @Produces({"text/uri-list", "application/json"})
    public String getDatasetList(UriInfo ui, String accept) {
        String result = "";
        int i = 0;
        System.out.println("accept header string is: " + accept);
        mongoCollection = mongoDB.getCollection("dataset");
        try (MongoCursor<Document> cursor = mongoCollection.find().iterator()) {
            while(cursor.hasNext()) {
                Document document = cursor.next();
                result += (ui.getBaseUri() + "dataset/" + document.get("_id") + "\n");
                i++;
            }
            cursor.close();
        } finally {

        }
        LOG.log(Level.INFO, "Retrieved " + i + " datasets\n" + result);
        return result;
    }

    public String getDatasetArff(String id){
        String output = "";
        mongoCollection = mongoDB.getCollection("dataset");

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Object datasetobj = mongoCollection.find(query).first();
        Gson gson = new Gson();
        String jsonString = gson.toJson(datasetobj);
        Dataset dataset = gson.fromJson(jsonString, Dataset.class);
        if (dataset == null) {
            throw new NotFoundException("Could not find Dataset with id:" + id);
        }
        return dataset.arff;
    }

    public void saveDataset(Document model) {
        mongoCollection = mongoDB.getCollection("dataset");
        String strictJSON = model.toJson();
        //TODO make loop for parsing dots
        //strictJSON = strictJSON.replaceAll("(\"[^\"]*)(\\.)([^\"]*\".:)", "$1\\(DOT\\)$3");
        //strictJSON = strictJSON.replaceAll("(\"[^\"]*)(\\.)([^\"]*\".:)", "$1\\(DOT\\)$3");

        while (strictJSON != strictJSON.replaceAll("(\"[^\"]*)(\\.)([^\"]*\".:)", "$1\\(DOT\\)$3")) {
            strictJSON = strictJSON.replaceAll("(\"[^\"]*)(\\.)([^\"]*\".:)", "$1\\(DOT\\)$3");
        }

        System.out.println(strictJSON);
        Document documentParsed = Document.parse(strictJSON);
        mongoCollection.insertOne(documentParsed);
    }



    public void close(){
        mongoClient.close();
    }

    public void setMongoClient(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }


}
