package io.swagger.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.api.factories.ApiFactory;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

@Path("/openapi")

@io.swagger.annotations.Api(description = "the api API")
public class Api {
    private final ApiService delegate;

    public Api(@Context ServletConfig servletContext) {
        ApiService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Api.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (ApiService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = ApiFactory.getApiApi();
        }

        this.delegate = delegate;
    }

    @GET
    @Path("/openapi.json")

    @Produces({"application/json", "application/ld+json"})
    @ApiOperation(value = "", notes = "Get swagger api in JSON", response = void.class, tags = {"api",})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Server Error")
    })
    public Response apiApiJsonGet(@Context SecurityContext securityContext, @Context UriInfo ui)
        throws NotFoundException, IOException {
        return delegate.apiApiJsonGet(securityContext, ui);
    }

}
