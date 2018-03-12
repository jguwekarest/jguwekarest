package unit;

import io.swagger.api.task.Task;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TaskTest {
    private static final Logger LOG = Logger.getLogger(Task.class.getName());

    @Test
    public void createTask() throws Exception {

        Task task = new Task(TaskTest.class.toString(), "Test Task", "Creating a test task") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    setStatus(Status.CANNCELED);
                    e.printStackTrace();
                } finally {
                    setStatus(Status.COMPLETED);
                }
            }
        };

        Assert.assertEquals(task.getTaskId().getClass(), ObjectId.class);

        Assert.assertEquals(task.getTitle(), "Test Task");
        Assert.assertEquals(task.getDescription(), "Creating a test task");
        Date now = new Date();
        Assert.assertTrue(task.getDate().before(now));
        Assert.assertEquals(task.getStatus(), Task.Status.ACCEPTED);
        task.start();
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        TimeUnit.SECONDS.sleep(3);
        Assert.assertEquals(task.getStatus(), Task.Status.COMPLETED);
    }

    @Test
    public void createTaskAndCancel() throws Exception {

        Task task = new Task(TaskTest.class.toString(), "Test Task", "Creating a test task") {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    setStatus(Status.CANNCELED);
                    e.printStackTrace();
                } finally {
                    setStatus(Status.COMPLETED);
                }
            }
        };

        Assert.assertEquals(task.getTaskId().getClass(), ObjectId.class);

        Assert.assertEquals(task.getTitle(), "Test Task");
        Assert.assertEquals(task.getDescription(), "Creating a test task");
        TimeUnit.SECONDS.sleep(1);
        Date now = new Date();
        Assert.assertTrue(task.getDate().before(now));
        Assert.assertEquals(task.getStatus(), Task.Status.ACCEPTED);
        task.start();
        Assert.assertEquals(task.getStatus(), Task.Status.RUNNING);
        task.stop();
        Assert.assertEquals(task.getStatus(), Task.Status.CANNCELED);
    }


    @Test
    public void statusTest() throws Exception {
        //function not in use
        System.out.println("printing the completed status: " + Task.Status.COMPLETED);
        Assert.assertEquals("COMPLETED", Task.Status.COMPLETED.toString());
    }

}
