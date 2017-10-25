package io.swagger.api;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/")

public class IndexPage {

        @Context ServletContext servletContext;
        @Context UriInfo ui;
        @Context HttpServletRequest servletRequest;

        // delivers the swagger ui index.html file
        @GET
        @Produces("text/html")
        public Response startPage() throws IOException {
            String contextBasePath = servletContext.getRealPath("/");
            return Response.ok(Files.readAllBytes(Paths.get(contextBasePath +"/index.html"))).build();
        }

}