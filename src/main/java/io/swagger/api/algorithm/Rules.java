package io.swagger.api.algorithm;

import io.swagger.annotations.*;
import io.swagger.api.factories.RulesFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;

@Path("/algorithm")
@Api(description = "the rules algorithm API")

public class Rules {

    private final RulesService delegate;

    public Rules(@Context ServletConfig servletContext) {
        RulesService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("Rules.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (RulesService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = RulesFactory.getRules();
        }
        this.delegate = delegate;
    }


    @POST
    @Path("/ZeroR")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/x-arff", "application/json"})
    @ApiOperation(value = "", notes = "REST interface to the WEKA ZeroR classifier.", response = void.class, tags={ "algorithm", }, position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response algorithmKNNclassificationPost(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
            ,@ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid
            ,@Context SecurityContext securityContext)
            throws io.swagger.api.NotFoundException, IOException {
        return delegate.algorithmZeroRPost(fileInputStream,fileDetail,subjectid,securityContext);
    }


}
