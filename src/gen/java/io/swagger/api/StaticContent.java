package io.swagger.api;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Path("/")
public class StaticContent {
    @Context
    ServletContext servletContext;
    @GET
    @Path("{path : .*\\.css}")
    @Produces("text/css;charset=UTF-8")
    public String staticContent(@PathParam("path") String path) throws IOException {
        String contextBasePath = new String(servletContext.getRealPath("/"));
        String content = new String(Files.readAllBytes(Paths.get(contextBasePath +"/"+ path)));
        return content;
    }

}
