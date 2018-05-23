package integration;

import com.google.gson.Gson;
import helper.TestHelper;
import io.swagger.api.data.ModelService;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class TreesTest {


    @Test(description = "Post an arff file to J48 algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmJ48( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/J48";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        /*
        curl -X POST "https://cuttlefish.informatik.uni-mainz.de/algorithm/J48" -H  "accept: text/x-arff" -H  "Content-Type: multipart/form-data"
        -F "file=@weather.numeric.arff;type=" -F "binarySplits=0" -F "confidenceFactor=0.25" -F "minNumObj=2" -F "numFolds=3"
        -F "reducedErrorPruning=0" -F "seed=1" -F "subtreeRaising=1" -F "unpruned=1" -F "useLaplace=0"
        */

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("binarySplits", "0")
            .field("confidenceFactor", "0.25")
            .field("minNumObj", "2")
            .field("numFolds", "3")
            .field("reducedErrorPruning", "0")
            .field("seed", "1")
            .field("subtreeRaising", "1")
            .field("unpruned", "1")
            .field("useLaplace", "0");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));

        String task_uri = response.readEntity(String.class);
        Assert.assertTrue(task_uri.matches(host + "/task/[a-fA-F\\d]{24}$"));

        final WebTarget taskTarget = client.target(task_uri);
        Invocation.Builder taskRequest = taskTarget.request();
        taskRequest.accept(MediaType.APPLICATION_JSON);

        Response taskResponse = taskRequest.get();

        int i = 0;
        while(taskResponse.getStatus() != 200){
            i += 1;
            TimeUnit.SECONDS.sleep(1);
            taskResponse = taskRequest.get();
            if (i>20) break;
        }
        TimeUnit.SECONDS.sleep(6);
        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(taskRemote.getStep(), Task.Step.SAVED);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 100f);

        String model_uri = taskRemote.getResultURI();

        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));


        // check new model String
        String savedModelString = TestHelper.getArff("J48.model.txt");

        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        final Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " not available.");
        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"), "Model at host: " + model_uri + " not available in mime-type text/plain. Is: " + modelResponse.getMediaType().toString());
        Assert.assertEquals(modelResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));


        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("sunny,85,85,FALSE,no,1.0\n" +
            "sunny,80,90,TRUE,no,1.0\n" +
            "overcast,83,86,FALSE,yes,0.0\n" +
            "rainy,70,96,FALSE,yes,0.0\n" +
            "rainy,68,80,FALSE,yes,0.0\n" +
            "rainy,65,70,TRUE,no,1.0\n" +
            "overcast,64,65,TRUE,yes,0.0\n" +
            "rainy,72,95,TRUE,no,1.0\n" +
            "sunny,69,70,FALSE,yes,0.0\n" +
            "rainy,75,80,FALSE,yes,0.0\n" +
            "sunny,75,70,TRUE,yes,0.0\n" +
            "overcast,72,90,FALSE,yes,0.0\n" +
            "overcast,81,75,FALSE,yes,0.0\n" +
            "overcast,71,91,TRUE,no,0.0"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);
    }


    @Test(description = "Post an arff file to J48 with Ada Boost M1 algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmJ48AdaBoost( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/J48/adaboost";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        /*
           curl -X POST "http://0.0.0.0:8081/algorithm/J48/adaboost" -H  "accept: text/x-arff" -H  "Content-Type: multipart/form-data"
           -F "file=@weather.numeric.arff;type=" -F "batchSize=100" -F "numIterations=10" -F "useResampling=0" -F "weightThreshold=100"
           -F "binarySplits=0" -F "confidenceFactor=0.25" -F "minNumObj=2" -F "numFolds=3" -F "reducedErrorPruning=0" -F "seed=1"
           -F "subtreeRaising=1" -F "unpruned=1" -F "useLaplace=0"
        */

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart
            .field("batchSize", "100")
            .field("numIterations", "10")
            .field("useResampling", "1")
            .field("weightThreshold", "100")
            .field("binarySplits", "0")
            .field("confidenceFactor", "0.25")
            .field("minNumObj", "2")
            .field("numFolds", "3")
            .field("reducedErrorPruning", "0")
            .field("seed", "1")
            .field("subtreeRaising", "1")
            .field("unpruned", "1")
            .field("useLaplace", "0");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));

        String task_uri = response.readEntity(String.class);
        Assert.assertTrue(task_uri.matches(host + "/task/[a-fA-F\\d]{24}$"));

        final WebTarget taskTarget = client.target(task_uri);
        Invocation.Builder taskRequest = taskTarget.request();
        taskRequest.accept(MediaType.APPLICATION_JSON);

        Response taskResponse = taskRequest.get();

        int i = 0;
        while(taskResponse.getStatus() != 200){
            i += 1;
            TimeUnit.SECONDS.sleep(1);
            taskResponse = taskRequest.get();
            if (i>20) break;
        }
        TimeUnit.SECONDS.sleep(6);
        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(taskRemote.getStep(), Task.Step.SAVED);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 100f);

        String model_uri = taskRemote.getResultURI();

        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));


        // check new model String
        String savedModelString = TestHelper.getArff("J48adaboost.model.txt");

        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        final Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " not available.");
        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"), "Model at host: " + model_uri + " not available in mime-type text/plain. Is: " + modelResponse.getMediaType().toString());
        Assert.assertEquals(savedModelString.replaceAll("(?m) +$","").trim(),modelResponse.readEntity(String.class).replaceAll("(?m) +$","").trim());

        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("sunny,85,85,FALSE,no,1.0\n" +
            "sunny,80,90,TRUE,no,1.0\n" +
            "overcast,83,86,FALSE,yes,0.0\n" +
            "rainy,70,96,FALSE,yes,0.0\n" +
            "rainy,68,80,FALSE,yes,0.0\n" +
            "rainy,65,70,TRUE,no,1.0\n" +
            "overcast,64,65,TRUE,yes,0.0\n" +
            "rainy,72,95,TRUE,no,1.0\n" +
            "sunny,69,70,FALSE,yes,0.0\n" +
            "rainy,75,80,FALSE,yes,0.0\n" +
            "sunny,75,70,TRUE,yes,0.0\n" +
            "overcast,72,90,FALSE,yes,0.0\n" +
            "overcast,81,75,FALSE,yes,0.0\n" +
            "overcast,71,91,TRUE,no,0.0\n"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);
    }


    @Test(description = "Post an arff file to J48 with Bagging meta algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmJ48Bagging( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/J48/bagging";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        /*
           curl -X POST "http://0.0.0.0:8081/algorithm/J48/adaboost" -H  "accept: text/x-arff" -H  "Content-Type: multipart/form-data"
           -F "file=@weather.numeric.arff;type=" -F "bagSizePercent=75" -F "batchSize=100" -F "numIterations=10"
           -F "binarySplits=0" -F "confidenceFactor=0.25" -F "minNumObj=2" -F "numFolds=3" -F "reducedErrorPruning=0" -F "seed=1"
           -F "subtreeRaising=1" -F "unpruned=1" -F "useLaplace=0"
        */

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart
            .field("bagSizePercent", "75")
            .field("batchSize", "100")
            .field("numIterations", "10")
            .field("binarySplits", "0")
            .field("confidenceFactor", "0.25")
            .field("minNumObj", "2")
            .field("numFolds", "3")
            .field("reducedErrorPruning", "0")
            .field("seed", "1")
            .field("subtreeRaising", "1")
            .field("unpruned", "1")
            .field("useLaplace", "0");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200, "Task status should be 200 but is: " + response.getStatus());
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));

        String task_uri = response.readEntity(String.class);
        Assert.assertTrue(task_uri.matches(host + "/task/[a-fA-F\\d]{24}$"));

        final WebTarget taskTarget = client.target(task_uri);
        Invocation.Builder taskRequest = taskTarget.request();
        taskRequest.accept(MediaType.APPLICATION_JSON);

        Response taskResponse = taskRequest.get();

        int i = 0;
        while(taskResponse.getStatus() != 200){
            i += 1;
            TimeUnit.SECONDS.sleep(1);
            taskResponse = taskRequest.get();
            if (i>20) break;
        }
        TimeUnit.SECONDS.sleep(6);
        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.COMPLETED, "Task is not COMPLETED. Is: " + taskRemote.getStatus() + " in step: " +taskRemote.getStep());
        Assert.assertEquals(taskRemote.getStep(), Task.Step.SAVED);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 100f);

        String model_uri = taskRemote.getResultURI();

        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));


        // check new model String
        String savedModelString = TestHelper.getArff("J48bagging.model.txt");

        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        final Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " not available.");
        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"), "Model at host: " + model_uri + " not available in mime-type text/plain. Is: " + modelResponse.getMediaType().toString());
        Assert.assertEquals(modelResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));

        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("sunny,85,85,FALSE,no,0.0\n" +
            "sunny,80,90,TRUE,no,0.0\n" +
            "overcast,83,86,FALSE,yes,0.0\n" +
            "rainy,70,96,FALSE,yes,0.0\n" +
            "rainy,68,80,FALSE,yes,0.0\n" +
            "rainy,65,70,TRUE,no,0.0\n" +
            "overcast,64,65,TRUE,yes,0.0\n" +
            "rainy,72,95,TRUE,no,1.0\n" +
            "sunny,69,70,FALSE,yes,0.0\n" +
            "rainy,75,80,FALSE,yes,0.0\n" +
            "sunny,75,70,TRUE,yes,0.0\n" +
            "overcast,72,90,FALSE,yes,0.0\n" +
            "overcast,81,75,FALSE,yes,0.0\n" +
            "overcast,71,91,TRUE,no,0.0"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);
    }



    @Test(description = "Post an arff file to M5P algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmM5PPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/M5P";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("diabetes.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("minNumInstances", "6.0")
            .field("unpruned", "0")
            .field("useUnsmoothed", "1")
            .field("buildRegressionTree", "1");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("save", "false").bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        String task_uri = response.readEntity(String.class);
        Assert.assertTrue(task_uri.startsWith(host + "/task/"), "Task URI is: " + task_uri + ". It do not start with: " + host + "/task/");

        final WebTarget taskTarget = client.target(task_uri);
        Invocation.Builder taskRequest = taskTarget.request();
        taskRequest.accept(MediaType.APPLICATION_JSON);

        Response taskResponse = taskRequest.get();
        Assert.assertTrue(taskResponse.getStatus() == 202, "Task at host: " + task_uri + " has wrong http code. Code is: " + taskResponse.getStatus());
        Assert.assertTrue(taskResponse.getMediaType().toString().equals(MediaType.APPLICATION_JSON), "Task at host: " + task_uri + " not available in mime-type application/json. Is: " + taskResponse.getMediaType().toString());
        //Assert.assertEquals(taskResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));
        int i = 0;
        while(taskResponse.getStatus() != 200){
            i += 1;
            TimeUnit.SECONDS.sleep(1);
            taskResponse = taskRequest.get();
            if (i>20) break;
        }
        Assert.assertTrue(taskResponse.getStatus() == 200, "Task at host: " + task_uri + " has wrong http code. Code is: " + taskResponse.getStatus());

        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(taskRemote.getStep(), Task.Step.SAVED);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 100f);

        String model_uri = taskRemote.getResultURI();
        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " has wrong http code. Code is: " + modelResponse.getStatus());

        String model_text = modelResponse.readEntity(String.class);

        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"));

        Assert.assertTrue(model_text.contains("M5 pruned regression tree:\n" +
            "\n" +
            "age <= 5.15 : LM1 (9/85.03%)\n" +
            "age >  5.15 : LM2 (34/83.991%)\n" +
            "\n" +
            "LM num: 1\n" +
            "c_peptide = \n" +
            "\t+ 4\n" +
            "\n" +
            "LM num: 2\n" +
            "c_peptide = \n" +
            "\t+ 4.9441\n" +
            "\n" +
            "Number of Rules : 2"));

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }

    @Test(description = "Post an arff file to RandomForest algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmRandomForestPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/RandomForest";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("storeOutOfBagPredictions", "false")
            .field("numExecutionSlots", "1")
            .field("numDecimalPlaces", "2")
            .field("bagSizePercent", "100")
            .field("batchSize", "100")
            .field("printClassifiers", "false")
            .field("numIterations", "100")
            .field("outputOutOfBagComplexityStatistics", "false")
            .field("breakTiesRandomly", "false")
            .field("maxDepth", "0")
            .field("computeAttributeImportance", "false")
            .field("calcOutOfBag", "false")
            .field("numFeatures", "0")
            .field("validation", "CrossValidation")
            .field("validationNum", "10");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("save", "false").bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        String task_uri = response.readEntity(String.class);
        Assert.assertTrue(task_uri.startsWith(host + "/task/"), "Task URI is: " + task_uri + ". It do not start with: " + host + "/task/");

        final WebTarget taskTarget = client.target(task_uri);
        Invocation.Builder taskRequest = taskTarget.request();
        taskRequest.accept(MediaType.APPLICATION_JSON);

        Response taskResponse = taskRequest.get();
        Assert.assertTrue(taskResponse.getStatus() == 202, "Task at host: " + task_uri + " has wrong http code. Code is: " + taskResponse.getStatus());
        Assert.assertTrue(taskResponse.getMediaType().toString().equals(MediaType.APPLICATION_JSON), "Task at host: " + task_uri + " not available in mime-type application/json. Is: " + taskResponse.getMediaType().toString());
        //Assert.assertEquals(taskResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));
        int i = 0;
        while(taskResponse.getStatus() != 200){
            i += 1;
            TimeUnit.SECONDS.sleep(1);
            taskResponse = taskRequest.get();
            if (i>20) break;
        }
        Assert.assertTrue(taskResponse.getStatus() == 200, "Task at host: " + task_uri + " has wrong http code. Code is: " + taskResponse.getStatus());

        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(taskRemote.getStep(), Task.Step.SAVED);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 100f);

        String model_uri = taskRemote.getResultURI();
        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " has wrong http code. Code is: " + modelResponse.getStatus());

        String model_text = modelResponse.readEntity(String.class);

        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"));

        Assert.assertTrue(model_text.contains("Correctly Classified Instances           9               64.2857 %\n" +
            "Incorrectly Classified Instances         5               35.7143 %\n" +
            "Kappa statistic                          0.186 \n" +
            "Mean absolute error                      0.4733\n" +
            "Root mean squared error                  0.5221\n" +
            "Relative absolute error                 99.3961 %\n" +
            "Root relative squared error            105.8227 %\n" +
            "Total Number of Instances               14"),"Compared string is not in: " + model_text);

        Assert.assertTrue(model_text.contains("TP Rate  FP Rate  Precision  Recall   F-Measure  MCC      ROC Area  PRC Area  Class\n" +
            "                 0,778    0,600    0,700      0,778    0,737      0,189    0,444     0,669     yes\n" +
            "                 0,400    0,222    0,500      0,400    0,444      0,189    0,444     0,385     no\n" +
            "Weighted Avg.    0,643    0,465    0,629      0,643    0,632      0,189    0,444     0,567"),"Compared string is not in: " + model_text);

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }
}
