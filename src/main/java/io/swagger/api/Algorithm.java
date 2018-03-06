package io.swagger.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.api.factories.AlgorithmFactory;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;

import static io.swagger.api.Constants.TEXT_URILIST;

@Path("/algorithm")

@io.swagger.annotations.Api(description = "the algorithm API")
public class Algorithm {
    private final AlgorithmService delegate;

    public Algorithm(@Context ServletConfig servletContext) {
        AlgorithmService delegate = null;

        if (servletContext != null) {
            String implClass = servletContext.getInitParameter("AlgorithmApi.implementation");
            if (implClass != null && !"".equals(implClass.trim())) {
                try {
                    delegate = (AlgorithmService) Class.forName(implClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (delegate == null) {
            delegate = AlgorithmFactory.getAlgorithm();
        }
        this.delegate = delegate;
    }

    /*
        @GET
        @Path("/{algorithmname}")

        @Produces({ "application/json" })
        @ApiOperation(value = "", notes = "Get algorithm representation", response = void.class, tags={ "algorithm", })
        @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
        public Response algorithmAlgorithmnameGet(@ApiParam(value = "requested Content-Type" ,required=true, allowableValues="application/json")@HeaderParam("Accept") String accept
            ,@ApiParam(value = "name of an algorithm",required=true) @PathParam("algorithmname") String algorithmname
            ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
            ,@Context SecurityContext securityContext, @Context ServletContext servletContext)
                throws javax.ws.rs.NotFoundException, IOException, NotFoundException {
                return delegate.algorithmAlgorithmnameGet(accept,algorithmname,subjectid,securityContext,servletContext);
        }
        @POST
        @Path("/{algorithmname}")
        @Consumes({ "multipart/form-data" })
        @Produces({ "application/json" })
        @ApiOperation(value = "", notes = "Train data on algorithm", response = void.class, tags={ "algorithm", })
        @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
        public Response algorithmAlgorithmnamePost(@ApiParam(value = "SMILES identifier or Nanoparticle URI or comma separated list of SMILES  identifiers or Nanoparticle URI", required=true)@FormDataParam("identifier")  String identifier
            ,@ApiParam(value = "name of an algorithm",required=true) @PathParam("algorithmname") String algorithmname
            ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
            ,@Context SecurityContext securityContext)
                throws javax.ws.rs.NotFoundException, NotFoundException {
                return delegate.algorithmAlgorithmnamePost(identifier,algorithmname,subjectid,securityContext);
        }

    */
    @GET
    @Produces({TEXT_URILIST, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get a list of all available algorithms.",
        notes = "Get a list of all algorithms.",
        tags = {"algorithm"}
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Resource Not Found"),
        @ApiResponse(code = 500, message = "Server Error")})
    public Response algorithmGet(
        @ApiParam(value = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context SecurityContext securityContext,
        @Context UriInfo uriinfo,
        @Context HttpHeaders headers)
        throws NotFoundException, IOException {

            String accept = headers.getRequestHeaders().getFirst("accept");
            return delegate.algorithmGet(accept, subjectid, securityContext, uriinfo);
    }

}