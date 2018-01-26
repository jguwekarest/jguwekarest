package io.swagger.api.authorization;

import io.swagger.annotations.*;
import io.swagger.api.Exeption;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/aa")
@Api(description = "Authentication A&A API")

public class Authorization {

    @POST
    @Path("/login")
    @Consumes({ "multipart/form-data" })
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @ApiOperation(
            value = "Request a security token.",
            notes = "Uses OpenAM server to get a security token.",
            tags={ "authorization", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Wrong, missing or insufficient credentials. Error report is produced."),
            @ApiResponse(code = 200, message = "Logged in - authentication token can be found in the response body (in JSON)")
    })
    public Response login(
            @ApiParam("Username") @FormDataParam("username") String username,
            @ApiParam("Password") @FormDataParam("password") String password,
            @Context HttpHeaders headers) throws Exeption.AAException {


        String token = AuthorizationService.login(username, password);
        String accept = headers.getRequestHeaders().getFirst("accept");
        if (accept.equals(MediaType.APPLICATION_JSON)) {
            JSONObject out = new JSONObject();
            out.put("subjectid", token);
            return Response.ok(out.toString())
                    .status(Response.Status.OK)
                    .build();
        }

        return Response
                .ok(token)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({ "application/x-www-form-urlencoded" })
    @ApiOperation(
            value = "Invalidate a security token.",
            notes = "Invalidates a security token and logs out the corresponding user.",
            tags={ "authorization", },
            produces = "text/plain")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Wrong, missing or invalid token."),
            @ApiResponse(code = 200, message = "Token invalidated.")
    })
    public Response logout(
            @HeaderParam("subjectid") String subjectId
    ) throws Exeption.AAException {
        boolean loggedOut = AuthorizationService.logout(subjectId);
        return Response
                .ok(loggedOut ? "true" : "false")
                .status(loggedOut ? Response.Status.OK : Response.Status.UNAUTHORIZED)
                .build();
    }

}
