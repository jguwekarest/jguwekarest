package integration;

import com.google.gson.Gson;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskHandler;
import io.swagger.api.data.TaskService;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskTest {

    @Test(description = "create a task")
    @Parameters({"host"})
    public void createTask( @Optional String host ) throws Exception {
        String resultUri = "http://0.0.0.0:8081/test/1";
        TaskHandler task = new TaskHandler(unit.TaskTest.class.toString(), "Test Task", "Creating a test task - createTask", "http://0.0.0.0:8081/") {
            @Override
            public void run() {
                try {
                    setState(Task.Step.PREPARATION, 10f);
                    TimeUnit.SECONDS.sleep(20);
                    setResultURI(resultUri);
                    finish();
                } catch (InterruptedException e) {
                    setStatus(Task.Status.CANCELLED);
                    e.printStackTrace();
                }
            }
        };

        Assert.assertEquals(task.getTitle(), "Test Task");
        Assert.assertEquals(task.getDescription(), "Creating a test task - createTask");
        task.start();
        Date now = new Date();
        TimeUnit.SECONDS.sleep(2); //wait to have at least 1 s date diff
        Assert.assertTrue(task.getDate().before(now));
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        Assert.assertEquals(task.getPercentageCompleted(), 10f);

        String task_uri = task.getURI();
        Assert.assertTrue(task_uri.startsWith(host + "/task/"), "Task URI is: " + task_uri + ". It do not start with: " + host + "/task/");

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        final WebTarget taskTarget = client.target(task_uri);
        Invocation.Builder taskRequest = taskTarget.request();
        taskRequest.accept(MediaType.APPLICATION_JSON);

        final Response taskResponse = taskRequest.get();
        Assert.assertTrue(taskResponse.getStatus() == 202, "Task at host: " + task_uri + " not available.");
        Assert.assertTrue(taskResponse.getMediaType().toString().equals(MediaType.APPLICATION_JSON), "Task at host: " + task_uri + " not available in mime-type application/json. Is: " + taskResponse.getMediaType().toString());

        Gson gson = new Gson();
        String jsonString = taskResponse.readEntity(String.class);
        Task taskRemote = gson.fromJson(jsonString, Task.class);

        Assert.assertEquals(taskRemote.getStatus(), Task.Status.RUNNING);
        Assert.assertEquals(taskRemote.getStep(), Task.Step.PREPARATION);
        Assert.assertEquals(taskRemote.getPercentageCompleted(), 10f);
        //Assert.assertEquals(taskResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));

        Boolean taskdelete= TaskService.delete(taskRemote);
        Assert.assertTrue(taskdelete);

    }
}