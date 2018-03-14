package io.swagger.api.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import io.swagger.api.ErrorReport;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;

import static io.swagger.api.Constants.TEXT_URILIST;
import static io.swagger.api.data.TaskService.getTask;
import static io.swagger.api.data.TaskService.listTasks;

@Path("/")
@Api(description = "Task API")
public class Task {

    @Context
    UriInfo ui;

    @GET
    @Path("/task")
    @Produces({ TEXT_URILIST, MediaType.APPLICATION_JSON})
    @ApiOperation(
        value = "List all tasks.",
        notes = "List all tasks.",
        tags={ "task", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/task")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:URIList")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:URIList")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response list(
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
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
    @ApiOperation(
        value = "Get json representation of a task.",
        notes = "Get json representation of a task.",
        tags={ "task", },
        extensions = {
            @Extension(properties = {@ExtensionProperty(name = "orn-@id", value = "/task/{id}")}),
            @Extension(properties = {@ExtensionProperty(name = "orn-@type", value = "x-orn:Task")}),
            @Extension(name = "orn:expects", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:ID")}),
            @Extension(name = "orn:returns", properties = {@ExtensionProperty(name = "x-orn-@id", value = "x-orn:Task")})
        })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Resource Not Found") })
    public Response get(
        @ApiParam(value = "Task ID" )@PathParam("id") String id,
        @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui)
        throws ApiException, NotFoundException {

        Task out = getTask(id, ui, subjectid);

        return Response
            .ok(out)
            .status(Response.Status.OK)
            .build();
    }

    public enum Status { ACCEPTED,RUNNING,COMPLETED,CANCELLED,ERROR }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
    public String getTaskID() { return taskID; }

    @JsonProperty("taskID")
    String taskID;
    @JsonProperty("URI")
    String URI;
    @JsonProperty("resultURI")
    String resultURI;
    public Date date;
    @JsonProperty("creator")
    String creator;
    //public String waitingFor;
    @JsonProperty("title")
    String title;
    @JsonProperty("hasStatus")
    Status hasStatus;
    @JsonProperty("description")
    String description;
    @JsonProperty("percentageCompleted")
    Float percentageCompleted;

    public ErrorReport errorReport;

}