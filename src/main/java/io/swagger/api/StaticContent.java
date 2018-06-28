package io.swagger.api;

import io.swagger.v3.oas.annotations.Hidden;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Helper Class to deliver static web contents.
 */
@Path("/")
public class StaticContent {

    @Context ServletContext servletContext;
    @Context UriInfo ui;
    @Context HttpServletRequest servletRequest;

    // delivers the swagger ui index.html file
    @GET
    @Hidden
    @Path("/")
    @Produces("text/html")
    public Response startPage() throws IOException {
        String contextBasePath = servletContext.getRealPath("/");
        return Response.ok(Files.readAllBytes(Paths.get(contextBasePath +"/index.html"))).build();
    }

    // delivers static CSS and JavaScript file
    @GET
    @Hidden
    @Path("{path : .*\\.css|.*\\.js|.*\\.map}")
    @Produces("text/css;charset=UTF-8")
    public Response staticContent(@PathParam("path") String path) throws IOException {
        String contextBasePath = servletContext.getRealPath("/");
        //String content = Files.readAllBytes(Paths.get(StringUtil.checkTrailingSlash(contextBasePath) + path));
        return Response.ok(Files.readAllBytes(Paths.get(StringUtil.checkTrailingSlash(contextBasePath) + path))).build();
    }

    // delivers static PNG files
    @GET
    @Hidden
    @Path("{path : .*\\.png}")
    @Produces("image/png")
    public Response staticPngContent(@PathParam("path") String path) throws IOException {
        System.out.println("serve static png content: " + path);
        String contextBasePath = servletContext.getRealPath("/");
        FileInputStream content = new FileInputStream(StringUtil.checkTrailingSlash(contextBasePath) + path);
        return Response.ok(content).build();
    }

}
