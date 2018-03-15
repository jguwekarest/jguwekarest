package io.swagger.api.data;

import com.google.gson.Gson;
import org.bson.Document;

import javax.ws.rs.core.UriInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskService {

    private static final Logger LOG = Logger.getLogger(TaskService.class.getName());

    /**
     * Get a list of tasks or taskURIs from mongoDB.
     * @param ui UriInfos
     * @param accept requested mime-type
     * @param token security token
     * @return task list
     */
    static Object listTasks(UriInfo ui, String accept, String token) {
        Dao taskDao = new Dao();
        Object taskList = taskDao.listData("task", ui, accept);
        taskDao.close();
        return taskList;
    }


    /**
     * Get a task from mongoDB.
     * @param id task/mongodb id
     * @param token  security token
     * @return task object
     */
    public static Task getTask(String id, UriInfo ui, String token){
        Task task;
        Dao taskDao = new Dao();
        try {
            task = taskDao.getTask(id);
        }finally {
            taskDao.close();
        }
        return task;
    }

    /**
     * Save a task instance
     * @param task Task
     * @return uri of task
     */
    public static String save(Task task){
        Dao taskDao = new Dao();
        System.out.println("Task save :" + task.title);
        String id = null;
        try {
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(task, Task.class));
            id = taskDao.saveData("task", document);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            taskDao.close();
        }
        return id;
    }

    /**
     * Update task in mongodb
     * @param task Task
     * @return uri of task
     */
    public static Boolean update(Task task){
        Dao taskDao = new Dao();
        Boolean result;
        try {
            Gson gson = new Gson();
            Document document = Document.parse(gson.toJson(task, Task.class));
            result = taskDao.updateData("task", document, task.taskID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            taskDao.close();
        }
        return result;
    }

    /**
     * Delete task in mongodb
     * @param task Task to delete
     * @return uri of task
     */
    public static Boolean delete(Task task){
        Dao taskDao = new Dao();
        Boolean result;
        try {
          String id = task.taskID;
          result = taskDao.delete("task", id);
          LOG.log(Level.INFO,"task " + id + " is deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            taskDao.close();
        }
        return result;
    }
}
