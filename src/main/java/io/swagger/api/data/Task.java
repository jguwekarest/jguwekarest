package io.swagger.api.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import io.swagger.api.ApiException;
import io.swagger.api.ErrorReport;
import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;

import static io.swagger.api.Constants.TEXT_URILIST;
import static io.swagger.api.data.TaskService.getTask;
import static io.swagger.api.data.TaskService.listTasks;

@Path("/")
//@Api(description = "Task API")
public class Task {

    @Context
    UriInfo ui;

    @GET
    @Path("/task")
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(
        summary = "List all tasks.",
        description = "List all tasks.",
        tags={ "task", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/task")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:URIList")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:URIList")})
        })
    @GroupedApiResponsesOk
    public Response list(
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid,
        @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

        String accept = headers.getRequestHeaders().getFirst("accept");
        Object taskList = listTasks(ui, accept, subjectid);

        return Response
            .ok(taskList)
            .status(Response.Status.OK)
            .build();
    }


    @GET
    @Path("/task/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Operation(
        summary = "Get json representation of a task.",
        description = "Get json representation of a task.",
        tags={ "task", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/task/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Task")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")})
        })
    @GroupedApiResponsesOk
    public Response get(
        @Parameter(description = "Task ID" )@PathParam("id") String id,
        @Parameter(description = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui)
        throws ApiException, NotFoundException {

        Task out = getTask(id, ui, subjectid);
        if(out.getStatus() == Status.COMPLETED) {
            return Response.ok(out).status(Response.Status.OK).build();
        } else if (out.getStatus() == Status.ACCEPTED || out.getStatus() == Status.RUNNING){
            return Response.ok(out).status(Response.Status.ACCEPTED).build();
        } else {
            return Response.ok(out).build();
        }

    }

    /**
     * Status of task execution.
     */
    public enum Status { ACCEPTED,RUNNING,COMPLETED,CANCELLED,ERROR }
    public enum Step { PREPARATION,TRAINING,VALIDATION,PREDICTION,SAVED }

    public String getURI() { return URI; }
    public void   setURI(String URI) { URI = URI; }

    public Task.Status getStatus() { return hasStatus; }
    public void   setStatus(Task.Status status) { hasStatus = status; }

    public Date getDate() { return date; }

    public String getTitle() { return title; }
    public void   setTitle(String title) { title = title; }

    public String getDescription() { return description; }
    void   setDescription(String description) { description = description; }

    public Float getPercentageCompleted() { return percentageCompleted; }
    void  setPercentageCompleted(Float percentageCompleted) { percentageCompleted = percentageCompleted; }

    public Task.Step getStep() { return step; }

    public String getResultURI() { return resultURI; }
    public void   setResultURI(String resultURI) { resultURI = resultURI; }

    public String getTaskID() { return taskID; }

    public void setTaskID(String id) {
        taskID = id;
    }

    public ErrorReport getErrorReport() { return errorReport; }



    @JsonProperty("taskID")
    String taskID;
    @JsonProperty("URI")
    String URI;
    @JsonProperty("resultURI")
    String resultURI;
    public Date date;
    @JsonProperty("creator")
    String creator;
    @JsonProperty("step")
    Step step;
    @JsonProperty("title")
    String title;
    @JsonProperty("hasStatus")
    Status hasStatus;
    @JsonProperty("description")
    String description;
    @VisibleForTesting
    @JsonProperty("percentageCompleted")
    Float percentageCompleted;

    public ErrorReport errorReport;

}