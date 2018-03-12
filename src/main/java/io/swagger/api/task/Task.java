package io.swagger.api.task;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.internal.LinkedTreeMap;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.logging.Logger;

import static io.swagger.api.task.Task.Status.*;

public abstract class Task implements Runnable {

    private static final Logger LOG = Logger.getLogger(Task.class.getName());
    private Thread thread = null;

    public void start() {
        if (thread == null) {
            thread = new Thread(this, this.title);
            thread.start();
            this.hasStatus = RUNNING;
        }
    }

    public void stop(){
        this.hasStatus = CANNCELED;
        this.thread.interrupt();
    }

    public Task(String creator, String title, String description) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.setTaskId(new ObjectId());
        this.date = new Date();
        this.hasStatus = ACCEPTED;
    }

    public String getResultUri() { return resultUri; }
    public void setResultUri(String resultUri) { this.resultUri = resultUri; }

    public String getURI() { return "task/" + getTaskId(); }
    public void setURI(String URI) { this.URI = URI; }

    public Status getStatus() { return hasStatus; }
    public void setStatus(Status status) { this.hasStatus = status; }

    public Date getDate() { return date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public enum Status { ACCEPTED,RUNNING,COMPLETED,CANNCELED,ERROR }

    @JsonProperty("_id")
    public ObjectId getTaskId() { return Taskid; }

    @JsonProperty("_id")
    public void setTaskId(ObjectId id) {
        this.Taskid = id;
    }

    private ObjectId Taskid;
    private String URI;
    private String resultUri;
    private LinkedTreeMap errorReport;

    public Date date;
    private String creator;
    //public String waitingFor;
    private String title;
    private Status hasStatus;
    private String description;
    private String resultURI;

    private class errorReport {
        private String actor;
        private String errorCause;
        private String errorType;
        private Integer http_code;
        private String message;
        private String rest_params;
        private String backtrace;

        private errorReport() {
        }
    }

    /**
    * Percentage completed.
    **/
    private Float percentageCompleted;

}
