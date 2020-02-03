package org.kramerlab.wekarestapi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * Helper Class to deliver static web contents.
 */
@Path("/")
public class StaticContent {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticContent.class.getName());
    
    @Context
    ServletContext servletContext;
    
    /**
     * Delivers the Swagger UI HTML file
     * 
     * @return {@link Response} containing the rendered HTML page
     * @throws IOException
     */
    @GET
    @Hidden
    @Produces(MediaType.TEXT_HTML)
    public Response startPage() throws Exception {
        LOGGER.debug("Loading start page");
        
        String contextBasePath = servletContext.getRealPath("/");
        LOGGER.debug("Context base path: {}", contextBasePath);
        
        try {
            return Response.ok(Files.readAllBytes(Paths.get(contextBasePath, "index.html"))).build();
        } catch (InvalidPathException e) {
            LOGGER.error("Cannot convert/find index.html at given context base path: {}", contextBasePath);
            throw e;
        } catch (IOException | OutOfMemoryError | SecurityException e) {
            LOGGER.error("Cannot read index.html. IO/Security error or out of memory");
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
    
    // delivers static CSS and JavaScript file
    @GET
    @Hidden
    @Path("{path : .*\\.css|.*\\.js|.*\\.map}")
    @Produces("text/css;charset=UTF-8")
    public Response staticContent(@PathParam("path") String pageElement) throws Exception {
        LOGGER.debug("Loading page element: {}", pageElement);
        
        String contextBasePath = servletContext.getRealPath("/");
        try {
            return Response.ok(Files.readAllBytes(Paths.get(StringUtil.checkTrailingSlash(contextBasePath), pageElement)))
                           .build();
        } catch (InvalidPathException e) {
            LOGGER.error("Cannot find {} at given context base path: {}", pageElement, contextBasePath);
            throw e;
        } catch (IOException | OutOfMemoryError | SecurityException e) {
            LOGGER.error("Cannot read {}. IO/Security error or out of memory", pageElement);
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
    
    // delivers static PNG files
    @GET
    @Hidden
    @Path("{path : .*\\.png}")
    @Produces("image/png")
    public Response staticPngContent(@PathParam("path") String pageElement) throws Exception {
        LOGGER.debug("Loading page element: {}", pageElement);
        
        String contextBasePath = servletContext.getRealPath("/");
        try {
            FileInputStream content = new FileInputStream(StringUtil.checkTrailingSlash(contextBasePath) + pageElement);
            return Response.ok(content).build();
        } catch (FileNotFoundException | SecurityException e) {
            LOGGER.error("Cannot read {}. IO/Security error or file not found", pageElement);
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}
