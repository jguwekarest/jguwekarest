package io.swagger.api.data;



import io.swagger.api.ErrorReport;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.core.Context;
import java.util.Date;

import static io.swagger.api.data.Task.Status.*;

/**
 * <h3>TaskHandler class to execute background tasks</h3>
 * To execute long running jobs use TaskHandler class to run and control threads.<br />
 * Asynchronous jobs are handled via the TaskHandler class. Task data is stored in <strong>{@link io.swagger.api.data.Task}</strong> class.<br />
 * <b>Example:</b>
 * <pre>
 * {@code
 * TaskHandler task = new TaskHandler(TaskTest.class.toString(), "Task name", "The task description", "http://0.0.0.0:8081/") {
 *      &#64;Override
 *      public void run() {
 *          try {
 *              // here comes the code to run
 *              finish(); //saves task to COMPLETED, SAVED and 100%
 *          } catch (YourException e) {
 *              e.printStackTrace();
 *          }
 *      }
 *  };
 *
 * task.start(); //run the task
 *
 * }
 * </pre>
 */
public abstract class TaskHandler implements Runnable {
    @Context
    String uri;
    private Task task;

    private Thread thread = null;

    /**
     * Starts task, set hasStatus to RUNNING and percentageCompleted to 0
     * saves the Task task to mongodb
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this, task.title);
            thread.start();
            task.percentageCompleted = 0f;
            task.hasStatus = RUNNING;
            update();
        }
    }

    public void setState(Task.Step step, float percentageComplete){
        task.percentageCompleted = percentageComplete;
        task.step = step;
        update();
    }


    /**
     * To cancel a task after it was started
     * interupts thread and set hasStatus to Task.Status.CANCELLED
     */
    public void cancel(){
        this.thread.interrupt();
        task.hasStatus = CANCELLED;
        update();
    }

    /**
     * Call at end of task to
     * set percentageCompleted = 100, hasStatus = Task.Status.CANCELLED, step = Task.Step.SAVED
     * update task in mongodb
     */
    public void finish(){
        task.hasStatus = COMPLETED;
        task.percentageCompleted = 100f;
        task.step = Task.Step.SAVED;
        update();
    }

    /**
     *
     * @param creator name of the creator (e.g.: service or user)
     * @param title task title
     * @param description description
     * @param baseuri base URI of the webservice
     */
    protected TaskHandler(String creator, String title, String description, String baseuri) {
        this.uri = baseuri;
        task = new Task();
        task.creator = creator;
        task.title = title;
        task.description = description;
        task.date = new Date();
        task.hasStatus = ACCEPTED;
        save();
        if (this.uri != null){
            setURI(uri + "task/" + task.taskID);
        } else {
            setURI("task/" + task.taskID);
        }
    }

    public String getURI() { return task.URI; }
    public void   setURI(String URI) { task.URI = URI; }

    public Task.Status getStatus() { return task.hasStatus; }
    public void   setStatus(Task.Status status) { task.hasStatus = status; }

    public Date getDate() { return task.date; }

    public String getTitle() { return task.title; }
    public void   setTitle(String title) { task.title = title; }

    public String getDescription() { return task.description; }
    public void   setDescription(String description) { task.description = description; }

    public Float getPercentageCompleted() { return task.percentageCompleted; }
    public void  setPercentageCompleted(Float percentageCompleted) { task.percentageCompleted = percentageCompleted; }

    public Task.Step getStep() { return task.step; }

    public String getResultURI() { return task.resultURI; }
    protected void   setResultURI(String resultURI) { task.resultURI = resultURI; }

    public String getTaskID() { return task.taskID; }

    public void setTaskID(String id) {
        task.taskID = id;
    }

    public ErrorReport getErrorReport() { return task.errorReport; }

    /**
     * Create an error report for a failed task
     *
     * @param e Exception
     * @param http_code http code
     * @param actor calling method
     */
    protected void setErrorReport(Exception e, Integer http_code, String actor){
        task.errorReport = new ErrorReport();
        setStatus(ERROR);
        if(e.getMessage() != null) task.errorReport.message = e.getMessage();
        if(e.getCause() != null) task.errorReport.errorCause = e.getCause().toString();
        if(http_code != null) task.errorReport.http_code = http_code;
        if(e.getStackTrace() != null) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            task.errorReport.backtrace = stackTrace.substring(0,Math.min(stackTrace.length(), 400)) + (stackTrace.length() > 400 ? "\n ..." : "" );
        }
        if(actor != null) task.errorReport.actor = actor;
        update();
    }

    /**
     * Save task instance to mongodb
     */
    private void save(){
        task.taskID = TaskService.save(task);
    }

    /**
     * Update task in mongodb
     */
    private void update(){
        TaskService.update(task);
    }

    /**
     * Delete task in mongodb
     */
    public void delete(){
        TaskService.delete(task);
    }

}
