package io.swagger.api;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Path("/")
public class StaticContent {
    @Context
    ServletContext servletContext;
    @GET
    @Path("{path : .*\\.css|.*\\.js}")
    @Produces("text/css;charset=UTF-8")
    public Response staticContent(@PathParam("path") String path) throws IOException {
        System.out.println("serve static content: " + path);
        String contextBasePath = new String(servletContext.getRealPath("/"));
        String content = new String(Files.readAllBytes(Paths.get(StringUtil.checkTrailingSlash(contextBasePath) + path)));
        return Response.ok(content).build();
    }

    @GET
    @Path("{path : .*\\.png}")
    @Produces("image/png")
    public Response staticPngContent(@PathParam("path") String path) throws IOException {
        System.out.println("serve static png content: " + path);
        String contextBasePath = new String(servletContext.getRealPath("/"));
        FileInputStream content = new FileInputStream(StringUtil.checkTrailingSlash(contextBasePath) + path);
        return Response.ok(content).build();
    }

}
