package io.swagger.api;

import io.swagger.api.factories.ApiFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

@Path("/api")
//Api(description = "the api API")
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
    @Path("/api.json")
    @Produces({"application/json", "application/ld+json"})
    @Operation(description = "Get Open API representation in JSON", summary = "Get Open API representation in JSON", tags = {"api",})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Server Error")
    })
    public Response apiApiJsonGet(@Context SecurityContext securityContext, @Context UriInfo ui)
        throws NotFoundException, IOException {
        return delegate.apiApiJsonGet(securityContext, ui);
    }

}
