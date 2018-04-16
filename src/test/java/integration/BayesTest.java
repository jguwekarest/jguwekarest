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

public class BayesTest {

    @Test(description = "Post an arff file to BayesNet algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmBayesNetPost( @Optional  String host ) throws Exception {

        String uri = host + "/algorithm/BayesNet";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("estimator", "SimpleEstimator")
                         .field("estimatorParams", "0.5")
                         .field("useADTree", "0")
                         .field("searchAlgorithm", "local.K2")
                         .field("searchParams", "-P 1 -S BAYES");

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
        modelRequest.accept(MediaType.APPLICATION_JSON);

        Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " has wrong http code. Code is: " + modelResponse.getStatus());

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }

    @Test(description = "Post an arff file to BayesNet algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmBayesNetPostAndPredict( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/BayesNet";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("estimator", "SimpleEstimator")
                .field("estimatorParams", "0.5")
                .field("useADTree", "0")
                .field("searchAlgorithm", "local.K2")
                .field("searchParams", "-P 1 -S BAYES");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

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

        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(taskRemote.getStep(), Task.Step.SAVED);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 100f);

        String model_uri = taskRemote.getResultURI();

        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));

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
        Assert.assertTrue(prediction_text.contains("overcast,83,86,FALSE,yes,0.0"));
        Assert.assertTrue(prediction_text.contains("sunny,75,70,TRUE,yes,1.0"));
        Assert.assertFalse(prediction_text.contains("overcast,72,90,FALSE,yes,1.0"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);
    }



    @Test(description = "Post an arff file to NaiveBayes algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmNaiveBayesPost( @Optional  String host ) throws Exception {

        String uri = host + "/algorithm/NaiveBayes";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("batchSize", "100")
            .field("useKernelEstimator", "0")
            .field("useSupervisedDiscretization", "0");

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

        // check new model String
        String savedModelString = TestHelper.getArff("NaiveBayes.model.txt");
        Assert.assertEquals(modelResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }

    @Test(description = "Try to delete with a none existing model id")
    public void deleteModelFalse() throws Exception {
        String id = "1234567890abcdef12345678";
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertFalse(resultDelete);
    }
}
