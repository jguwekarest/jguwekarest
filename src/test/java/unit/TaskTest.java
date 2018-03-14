package unit;

import io.swagger.api.ErrorReport;
import io.swagger.api.data.Task;
import io.swagger.api.data.TaskHandler;
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

    @Test(description = "create a task, check if running and completed")
    public void createTask() throws Exception {
        String resultUri = "http://0.0.0.0:8081/test/1";

        TaskHandler task = new TaskHandler(TaskTest.class.toString(), "Test Task", "Creating a test task - createTask", "http://0.0.0.0:8081/") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    setResultURI(resultUri);
                    finalize();
                } catch (InterruptedException e) {
                    setStatus(Task.Status.CANCELLED);
                    e.printStackTrace();
                }
            }
        };

        Assert.assertEquals(task.getTitle(), "Test Task");
        Assert.assertEquals(task.getDescription(), "Creating a test task - createTask");
        Date now = new Date();
        TimeUnit.SECONDS.sleep(1); //wait to have at least 1 s date diff
        Assert.assertTrue(task.getDate().before(now));
        Assert.assertEquals(task.getStatus(), Task.Status.ACCEPTED);
        Assert.assertEquals(task.getPercentageCompleted(), null);
        task.start();
        Assert.assertEquals(task.getTaskID().getClass(), String.class);
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        Assert.assertEquals(task.getPercentageCompleted(), 0f);
        TimeUnit.SECONDS.sleep(3);
        Assert.assertEquals(task.getStatus(), Task.Status.COMPLETED);
        Assert.assertEquals(task.getPercentageCompleted(), 100f);
        Assert.assertEquals(task.getResultURI(), resultUri);
        task.delete();
    }

    @Test
    public void createTaskAndCancel() throws Exception {

        TaskHandler task = new TaskHandler(TaskTest.class.toString(), "Test Task", "Creating a test task - createTaskAndCancel", "http://0.0.0.0:8081/") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(8);
                    finalize();
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
                    finalize();
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

}
