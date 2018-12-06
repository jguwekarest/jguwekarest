package integration;

import com.google.gson.Gson;
import helper.TestHelper;
import io.swagger.api.authorization.AuthorizationService;
import io.swagger.api.data.Dao;
import io.swagger.api.data.Dataset;
import io.swagger.api.data.DatasetService;
import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;

import static io.swagger.api.Constants.TEXT_ARFF;

public class DatasetTest {


    //private static final String EXT_DATASET = "http://dev.jaqpot.org:8081/jaqpot/services/dataset/yzsAXE5rLPzz";
    private static final String EXT_DATASET = "https://api-jaqpot.prod.openrisknet.org/jaqpot/services/dataset/Gajewicz_10_29_class";
    // Has Errors: Authorization header not provided. Please provide header and token
    // needs --header 'Authorization: Bearer <oAuthToken>' for the ORN web authentication
    // Tests are disabled 12/2018

    @Parameters({"host"})
    //@Test(description = "Get an external dataset and convert it to arff.")
    public void loadExternalDatasetToArff( @Optional  String host ) throws Exception {
        String uri = host + "/dataset";
        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        String token = AuthorizationService.login("guest","guest");

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("dataset_uri", EXT_DATASET)
                         .field("subjectid", token);

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart;

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/x-arff");
        request.header("subjectid", token);

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String dataset = response.readEntity(String.class);
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(dataset.contains("meta contributors: Yoram Cohen, Hongbo Guo,"));
        Assert.assertTrue(dataset.contains("0.147,22.64,-24.08,197.0,44.43,35.03,4.79,0.022,35.03,-5.505,0.0,-6.73,0.224,22.32,14.9,0.275,0.0,247.019,10.0,0.0,63.72,19.1,518.33,0.184,34.07,44.8"));

    }

    @Parameters({"host"})
    //@Test(description = "Get an external dataset, ..., save it and delete it.")
    public void loadExternalDatasetToArffSaveAndDelete( @Optional  String host ) throws Exception {
        String uri = host + "/dataset";
        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        String token = AuthorizationService.login("guest","guest");

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("dataset_uri", EXT_DATASET)
                .field("subjectid", token);

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart;

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");
        request.header("subjectid", token);

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String dataset_uri = response.readEntity(String.class);
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        System.out.println("Dataset URI of the saved external dataset is: " + dataset_uri);
        Assert.assertTrue(dataset_uri.matches(host + "/dataset/[a-fA-F\\d]{24}"));
        String id = dataset_uri.substring(dataset_uri.length() - 24);
        Boolean resultDelete = DatasetService.deleteDataset(id);
        Assert.assertTrue(resultDelete);

    }


    @Parameters({"host"})
    @Test(description = "Filter dataset.")
    public void filterDataset( @Optional  String host ) throws Exception {
        String uri = host + "/dataset/";

        String id = "";
        Dataset dataset = new Dataset();
        Dao datasetDao = new Dao();

        String arff = TestHelper.getArff("weather.numeric.arff");
        try {
            dataset.arff = arff;
            dataset.comment = "integration test dataset";
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(dataset));
            id = datasetDao.saveData("dataset", document);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            datasetDao.close();
        }

        try {
            String newArff = DatasetService.filter(dataset, null, null, null, true, null, null,null,null,null, "text/arff", uri);
            String filteredArff = TestHelper.getArff("weather.numeric.standardized.arff");
            Assert.assertEquals(newArff, filteredArff);

            newArff = DatasetService.filter(dataset, "1,2", null, null, null, null, null, null,null,null,"text/arff", uri);
            filteredArff = TestHelper.getArff("weather.numeric.removed.arff");
            Assert.assertEquals(newArff, filteredArff);


            final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

            //String token = AuthorizationService.login("guest","guest");

            FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
            formDataMultiPart.field("id", id)
                    .field("standardize", "true")
                    .field("ignore", "false");

            final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart;

            final WebTarget target = client.target(uri + id);
            Invocation.Builder request = target.request();
            request.accept(TEXT_ARFF);

            final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

            formDataMultiPart.close();
            multipart.close();

            newArff = response.readEntity(String.class);
            Assert.assertTrue(response.getStatus() == 200);
            Assert.assertTrue(response.getMediaType().toString().equals(TEXT_ARFF));
            filteredArff = TestHelper.getArff("weather.numeric.standardized.arff");
            Assert.assertEquals(newArff, filteredArff);

        } finally {
            Boolean resultDelete = DatasetService.deleteDataset(id);
            Assert.assertTrue(resultDelete);
        }
    }

    @Parameters({"host"})
    @Test(description = "Filter dataset.")
    public void filterDatasetDiscretize( @Optional  String host ) throws Exception {
        String uri = host + "/dataset/";
        String id = "";
        Dataset dataset = new Dataset();
        Dao datasetDao = new Dao();

        String arff = TestHelper.getArff("weather.numeric.arff");
        try {
            dataset.arff = arff;
            dataset.comment = "integration test dataset";
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(dataset));
            id = datasetDao.saveData("dataset", document);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            datasetDao.close();
        }
        try {
            String newArff = DatasetService.filter(dataset, null, null, null, null, null, null,"2",2,true, "text/arff", uri);
            String filteredArff = TestHelper.getArff("weather.numeric.discretized.arff");
            Assert.assertEquals(newArff + "\n", filteredArff);

            final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

            //String token = AuthorizationService.login("guest","guest");

            FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
            formDataMultiPart.field("id", id)
                .field("attributeIndicies", "2")
                .field("bins", "2")
                .field("useEqualFrequency", "true");

            final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart;

            final WebTarget target = client.target(uri + id);
            Invocation.Builder request = target.request();
            request.accept(TEXT_ARFF);

            final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

            formDataMultiPart.close();
            multipart.close();

            newArff = response.readEntity(String.class);
            Assert.assertTrue(response.getStatus() == 200);
            Assert.assertTrue(response.getMediaType().toString().equals(TEXT_ARFF));
            filteredArff = TestHelper.getArff("weather.numeric.discretized.arff");
            Assert.assertEquals(newArff + "\n", filteredArff);

        } finally {
            Boolean resultDelete = DatasetService.deleteDataset(id);
            Assert.assertTrue(resultDelete);
        }
    }

    @Test(description = "Try to delete with a none existing model id")
    public void deleteDatasetFalse() throws Exception {
        String id = "1234567890abcdef12345678";
        Boolean resultDelete = DatasetService.deleteDataset(id);
        Assert.assertFalse(resultDelete);
    }


}
