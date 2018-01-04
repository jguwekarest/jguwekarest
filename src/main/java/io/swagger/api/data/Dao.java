package io.swagger.api.data;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h3>Data Access Object library</h3>
 * communicates with the mongodb
 * @author m.rautenberg
 */
public class Dao {

    private String  dbName;
    private String  dbHost;
    private Integer dbPort;
    private String  dbUser;
    private String  dbPassword;

    private MongoClient     mongoClient = null;
    private MongoDatabase   mongoDB;
    private MongoCollection<Document> mongoCollection;

    private static Properties dbProperties = new Properties();
    private static final Logger LOG = Logger.getLogger(Dao.class.getName());

    Dao() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream("config/db.properties");
        try {
            dbProperties.load(is);
            dbName = dbProperties.getProperty("db.name");
            dbHost = dbProperties.getProperty("db.host");
            dbPort = Integer.parseInt(dbProperties.getProperty("db.port"));
            if (dbProperties.getProperty("db.user") != null) dbUser = dbProperties.getProperty("db.user");
            if (dbProperties.getProperty("db.password") != null) dbPassword = dbProperties.getProperty("db.password");
            LOG.log(Level.INFO, "Database configuration read!");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "No DB properties file found!", ex);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Database configuration can not be loaded!");
        } finally {
            if (!Objects.equals(dbUser, "") && dbPassword != null) {
                MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(dbUser, dbName, dbPassword.toCharArray());
                mongoClient = new MongoClient(new ServerAddress(dbHost, dbPort), Collections.singletonList(mongoCredential));
            } else {
                mongoClient = new MongoClient(dbHost, dbPort);
            }
            mongoDB = mongoClient.getDatabase(dbName);
            LOG.log(Level.INFO, "Database configured and connection established successfully!");
        }
    }

    /**
     * Lists dataset or model - URI list or JSON
     * @param collection dataset or model
     * @param ui URI info object
     * @param accept requested mime-type
     * @return list of models or datasets
     */
    @Produces({"text/uri-list", "application/json"})
    public Object listData(String collection, UriInfo ui, String accept) {
        StringBuilder result = new StringBuilder();
        mongoCollection = mongoDB.getCollection(collection);
        switch (accept) {
            case "text/uri-list":
                try (MongoCursor<Document> cursor = mongoCollection.find().iterator()) {
                    while(cursor.hasNext()) {
                        Document document = cursor.next();
                        result.append(ui.getBaseUri()).append(collection).append("/").append(document.get("_id")).append("\n");
                    }
                    cursor.close();
                }
                return result.toString();
            case "application/json":

                final ArrayList<Document> results = new ArrayList<>();
                Block<Document> printBlock = document -> {
                    document.replace("_id", document.get("_id").toString());
                    document.put("URI", ui.getBaseUri() + collection + "/" + document.get("_id").toString());
                    results.add(document);
                };

                mongoCollection.find().projection(new Document("datasetURI", 1)
                    .append("meta", 1)
                    .append("_id", 1))
                  .forEach(printBlock);

                return results;

        }
        return null;
    }

    /**
     * Returns arff representation of a dataset
     *
     * @param id dataset ID to search
     * @return String arff
     */
    String getDatasetArff(String id){

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
        return dataset.comment + dataset.arff;
    }

    /**
     * Returns GSON model object
     * @param id model ID to search
     * @return GSON model representation
     */
    Model getModel(String id){
        mongoCollection = mongoDB.getCollection("model");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Object modelObj = mongoCollection.find(query).first();
        Gson gson = new Gson();
        String jsonString = gson.toJson(modelObj);
        Model model = gson.fromJson(jsonString, Model.class);
        if (model == null) {
            throw new NotFoundException("Could not find Model with id:" + id);
        }
        return model;
    }

    /**
     * Saves JSON to mongodb
     * @param collection to save to (e.G.: model or dataset)
     * @param document GSON of a dataset, model ...
     * @return String ID of the saved collection
     */
    String saveData(String collection, Document document) {
        mongoCollection = mongoDB.getCollection(collection);
        String strictJSON = document.toJson();

        while (!Objects.equals(strictJSON, strictJSON.replaceAll("(\"[^\"]*)(\\.)([^\"]*\".:)", "$1\\(DOT\\)$3"))) {
            strictJSON = strictJSON.replaceAll("(\"[^\"]*)(\\.)([^\"]*\".:)", "$1\\(DOT\\)$3");
        }

        Document documentParsed = Document.parse(strictJSON);
        mongoCollection.insertOne(documentParsed);
        return documentParsed.get("_id").toString();

    }


    /**
     * Close the conection of the mongoclient to the mongodb
     */
    public void close(){
        mongoClient.close();
    }

   // public void setMongoClient(final MongoClient mongoClient) {
   //     this.mongoClient = mongoClient;
   // }


}
