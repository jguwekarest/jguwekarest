package integration;

import com.google.gson.Gson;
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

public class FunctionsTest {

    @Test(description = "Post an arff file to Logistic algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmLogisticPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/logistic";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("ridge", "1.0E-8")
            .field("useConjugateGradientDescent", "true")
            .field("maxIts", "-1")
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

        Assert.assertTrue(model_text.contains("Logistic Regression with ridge parameter of 1.0E-8\n" +
            "Coefficients...\n" +
            "                         Class\n" +
            "Variable                   yes\n" +
            "==============================\n" +
            "outlook=sunny          -4.7538\n" +
            "outlook=overcast        9.8306\n" +
            "outlook=rainy          -3.9845\n" +
            "temperature            -0.0776\n" +
            "humidity               -0.1556\n" +
            "windy=FALSE             3.7315\n" +
            "Intercept              20.5622"));

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }


    @Test(description = "Post an arff file to MultilayerPerceptron algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmMultilayerPerceptronPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/MultilayerPerceptron";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

        formDataMultiPart.field("momentum", "0.2")
            .field(    "nominalToBinaryFilter", "true")
            .field("hiddenLayers", "a")
            .field("validationThreshold", "20")
            .field("normalizeAttributes", "true")
            .field("numDecimalPlaces", "0")
            .field("batchSize", "100")
            .field("decay", "true")
            .field("validationSetSize", "0")
            .field("trainingTime", "500")
            .field("normalizeNumericClass", "true")
            .field("learningRate", "0.3")
            .field("reset", "true")
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
        Assert.assertTrue(model_text.contains("Sigmoid Node 0\n" +
            "    Inputs    Weights\n" +
            "    Threshold    0.26982885978760257\n" +
            "    Node 2    0.15514278608355117\n" +
            "    Node 3    0.18876803923522797\n" +
            "    Node 4    0.19368101877866986\n" +
            "    Node 5    0.13685365034133318\n" +
            "Sigmoid Node 1\n" +
            "    Inputs    Weights\n" +
            "    Threshold    -0.2564039232258344\n" +
            "    Node 2    -0.1270526458326683\n" +
            "    Node 3    -0.15773355619611837\n" +
            "    Node 4    -0.1888933392630922\n" +
            "    Node 5    -0.22385902643457084"));

        Assert.assertTrue(model_text.contains("orrectly Classified Instances           9               64.2857 %\n" +
            "Incorrectly Classified Instances         5               35.7143 %\n" +
            "Kappa statistic                          0     \n" +
            "Mean absolute error                      0.472 \n" +
            "Root mean squared error                  0.4954\n" +
            "Relative absolute error                 99.1303 %\n" +
            "Root relative squared error            100.4216 %\n" +
            "Total Number of Instances               14"));

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }


    @Test(description = "Post an arff file to SMO algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmSMOPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/SMO";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("numFolds", "-1")
            .field("c", "1.0")
            .field("batchSize", "100")
            .field("kernel", "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007")
            .field("filterType", "0")
            .field("calibrator", "weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4")
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

        Assert.assertTrue(model_text.contains("Machine linear: showing attribute weights, not support vectors.\n" +
            "\n" +
            "         0.8436 * (normalized) outlook=sunny\n" +
            " +      -0.9535 * (normalized) outlook=overcast\n" +
            " +       0.1099 * (normalized) outlook=rainy\n" +
            " +       0.5276 * (normalized) temperature\n" +
            " +       0.771  * (normalized) humidity\n" +
            " +      -0.8901 * (normalized) windy=FALSE\n" +
            " -       0.8683\n"),"Compared string is not in: " + model_text);

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }

    @Test(description = "Post an arff file to SMOreg algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmSMORegPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/SMOreg";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("diabetes.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("c", "1.0")
            .field("batchSize", "100")
            .field("kernel", "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007")
            .field("filterType", "0")
            .field("regOptimizer", "weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1")
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

        Assert.assertTrue(model_text.contains("Correlation coefficient                  0.5364\n" +
            "Mean absolute error                      0.479 \n" +
            "Root mean squared error                  0.6028\n" +
            "Relative absolute error                 86.3403 %\n" +
            "Root relative squared error             82.5301 %\n" +
            "Total Number of Instances               43"),"Compared string is not in: " + model_text);

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }

  /*

  see Functions.java

    @Test(description = "Post an arff file to GaussianProcesses algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmGaussianProcessesPost( @Optional String host ) throws Exception {

        String uri = host + "/algorithm/GaussianProcesses";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("diabetes.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("batchSize", "100")
            .field("kernel", "weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007")
            .field("filterType", "0")
            .field("noise", "1.0")
            .field("validation", "CrossValidation")
            .field("validationNum", "10");

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

        Assert.assertTrue(model_text.contains("Correlation coefficient                  0.0141\n" +
            "Mean absolute error                      0.537 \n" +
            "Root mean squared error                  0.7258\n" +
            "Relative absolute error                 96.7944 %\n" +
            "Root relative squared error             99.357  %\n" +
            "Total Number of Instances               43"),"Compared string is not in: " + model_text);

        Assert.assertTrue(model_text.contains("Average Target Value : 0.4851421188630491\n" +
            "Inverted Covariance Matrix:\n" +
            "    Lowest Value = -0.07579825336723334\n" +
            "    Highest Value = 0.9991157622596833\n" +
            "Inverted Covariance Matrix * Target-value Vector:\n" +
            "    Lowest Value = -0.478284598702981\n" +
            "    Highest Value = 0.46648681733797204"),"Compared string is not in: " + model_text);

        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }
*/
}
