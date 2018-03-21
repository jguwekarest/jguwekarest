package unit;

import io.swagger.api.ErrorReport;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskHandler;
import io.swagger.api.data.TaskService;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskTest {
    private static final Logger LOG = Logger.getLogger(Task.class.getName());

    @Test(description = "create a task, run and complete it with all possible Steps")
    public void createTask() throws Exception {
        String resultUri = "http://0.0.0.0:8081/test/1";

        TaskHandler task = new TaskHandler(TaskTest.class.toString(), "Test Task", "Creating a test task - createTask", "http://0.0.0.0:8081/") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    setState(Task.Step.PREPARATION,10f);
                    Assert.assertEquals(getPercentageCompleted(), 10f);
                    Assert.assertEquals(getStep(), Task.Step.PREPARATION);
                    Task taskDB = TaskService.getTask(getTaskID(),null,null);
                    Assert.assertEquals(taskDB.getPercentageCompleted(), 10f);
                    Assert.assertEquals(taskDB.getStep(), Task.Step.PREPARATION);
                    TimeUnit.SECONDS.sleep(1);
                    setState(Task.Step.TRAINING,30f);
                    TimeUnit.SECONDS.sleep(1);
                    Assert.assertEquals(getPercentageCompleted(), 30f);
                    Assert.assertEquals(getStep(), Task.Step.TRAINING);
                    taskDB = TaskService.getTask(getTaskID(),null,null);
                    Assert.assertEquals(taskDB.getPercentageCompleted(), 30f);
                    Assert.assertEquals(taskDB.getStep(), Task.Step.TRAINING);
                    TimeUnit.SECONDS.sleep(1);
                    setState(Task.Step.VALIDATION,80f);
                    Assert.assertEquals(getPercentageCompleted(), 80f);
                    Assert.assertEquals(getStep(), Task.Step.VALIDATION);
                    taskDB = TaskService.getTask(getTaskID(),null,null);
                    Assert.assertEquals(taskDB.getPercentageCompleted(), 80f);
                    Assert.assertEquals(taskDB.getStep(), Task.Step.VALIDATION);
                    TimeUnit.SECONDS.sleep(1);
                    setState(Task.Step.PREDICTION,90f);
                    Assert.assertEquals(getPercentageCompleted(), 90f);
                    Assert.assertEquals(getStep(), Task.Step.PREDICTION);
                    taskDB = TaskService.getTask(getTaskID(),null,null);
                    Assert.assertEquals(taskDB.getPercentageCompleted(), 90f);
                    Assert.assertEquals(taskDB.getStep(), Task.Step.PREDICTION);
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
        TimeUnit.SECONDS.sleep(1); //wait to have at least 1 s date diff
        Date now = new Date();
        Assert.assertTrue(task.getDate().before(now), task.getDate().toString() + " is not before " + now.toString());
        Assert.assertEquals(task.getStatus(), Task.Status.ACCEPTED);
        Assert.assertEquals(task.getPercentageCompleted(), null);
        task.start();
        Assert.assertEquals(task.getTaskID().getClass(), String.class);
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        Assert.assertEquals(task.getPercentageCompleted(), 0f);
        Task taskDB = TaskService.getTask(task.getTaskID(),null,null);
        Assert.assertEquals(taskDB.getPercentageCompleted(), 0f);
        Assert.assertEquals(taskDB.getStatus(), Task.Status.RUNNING);
        TimeUnit.SECONDS.sleep(7);
        Assert.assertEquals(task.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(task.getPercentageCompleted(), 100f);
        Assert.assertEquals(task.getResultURI(), resultUri);
        Assert.assertEquals(task.getStep(), Task.Step.SAVED);
        taskDB = TaskService.getTask(task.getTaskID(),null,null);
        Assert.assertEquals(taskDB.getPercentageCompleted(), 100f);
        Assert.assertEquals(taskDB.getStatus(), Task.Status.COMPLETED);
        task.delete();
    }

    @Test
    public void createTaskAndCancel() throws Exception {

        TaskHandler task = new TaskHandler(TaskTest.class.toString(), "Test Task", "Creating a test task - createTaskAndCancel", "http://0.0.0.0:8081/") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(8);
                    finish();
                } catch (InterruptedException e) {
                    LOG.log(Level.INFO,"InterruptedException catched.");
                    setStatus(Task.Status.CANCELLED);
                    e.printStackTrace();
                }
            }
        };

        Assert.assertEquals(task.getDescription(), "Creating a test task - createTaskAndCancel");
        task.start();
        Assert.assertEquals(task.getTaskID().getClass(), String.class);
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        task.cancel();
        Assert.assertEquals(task.getStatus(), Task.Status.CANCELLED);
        task.delete();
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test()
    public void createTaskWithError() throws Exception {

        TaskHandler task = new TaskHandler(TaskTest.class.toString(), "Test Task", "Creating a test task - createTaskWithError", "http://0.0.0.0:8081/") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    thrown.expect(IndexOutOfBoundsException.class);
                    ArrayList emptyList = new ArrayList();
                    Object o = emptyList.get(0);
                    Assert.assertFalse(1==1,"Code should have thrown error before.");
                    TimeUnit.SECONDS.sleep(5);
                    finish();
                } catch (InterruptedException e) {
                    LOG.log(Level.INFO,"InterruptedException catched.");
                    setStatus(Task.Status.CANCELLED);
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e){
                    LOG.log(Level.INFO,"Exeption catched.");
                    Exception ex = new Exception(e);
                    setErrorReport(ex, 500, "Test");
                }
            }
        };

        Assert.assertEquals(task.getTitle(), "Test Task");
        Assert.assertEquals(task.getDescription(), "Creating a test task - createTaskWithError");
        task.start();
        Assert.assertEquals(task.getTaskID().getClass(), String.class);
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        TimeUnit.SECONDS.sleep(3);
        Assert.assertEquals(task.getStatus(), Task.Status.ERROR);
        ErrorReport eR = task.getErrorReport();
        Assert.assertTrue(eR.http_code == 500);
        Assert.assertEquals(eR.message, "java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
        Assert.assertTrue(eR.backtrace.startsWith("java.lang.Exception: java.lang.IndexOutOfBoundsException"));
        Assert.assertEquals(eR.actor, "Test");
        Assert.assertEquals(eR.errorCause, "java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
        task.delete();
    }

    @Test(description = "test available statuses")
    public void statusTest() throws Exception {
        Assert.assertEquals("ACCEPTED",  Task.Status.ACCEPTED.toString());
        Assert.assertEquals("RUNNING",   Task.Status.RUNNING.toString());
        Assert.assertEquals("CANCELLED", Task.Status.CANCELLED.toString());
        Assert.assertEquals("COMPLETED", Task.Status.COMPLETED.toString());
        Assert.assertEquals("ERROR",     Task.Status.ERROR.toString());
    }

    @Test(description = "test available steps")
    public void stepTest() throws Exception {
        Assert.assertEquals("PREPARATION", Task.Step.PREPARATION.toString());
        Assert.assertEquals("TRAINING",    Task.Step.TRAINING.toString());
        Assert.assertEquals("VALIDATION",  Task.Step.VALIDATION.toString());
        Assert.assertEquals("PREDICTION",  Task.Step.PREDICTION.toString());
        Assert.assertEquals("SAVED",       Task.Step.SAVED.toString());
    }

}
