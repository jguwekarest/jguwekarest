package io.swagger.api;

import io.swagger.api.annotations.GroupedApiResponsesOk;
import io.swagger.api.factories.AlgorithmFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;

import static io.swagger.api.Constants.TEXT_URILIST;

@Path("/algorithm")
//@io.swagger.annotations.Api(description = "the algorithm API")
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
        @Operation(value = "", notes = "Get algorithm representation", response = void.class, tags={ "algorithm", })
        @ApiResponses(value = {
            @ApiResponse(responseCode = 200, message = "OK", response = void.class),
            @ApiResponse(responseCode = 400, message = "Bad Request", response = void.class),
            @ApiResponse(responseCode = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(responseCode = 403, message = "Forbidden", response = void.class),
            @ApiResponse(responseCode = 404, message = "Resource Not Found", response = void.class) })
        public Response algorithmAlgorithmnameGet(@Parameter(value = "requested Content-Type" ,required=true, allowableValues="application/json")@HeaderParam("Accept") String accept
            ,@Parameter(value = "name of an algorithm",required=true) @PathParam("algorithmname") String algorithmname
            ,@Parameter(value = "authorization token" )@HeaderParam("subjectid") String subjectid
            ,@Context SecurityContext securityContext, @Context ServletContext servletContext)
                throws javax.ws.rs.NotFoundException, IOException, NotFoundException {
                return delegate.algorithmAlgorithmnameGet(accept,algorithmname,subjectid,securityContext,servletContext);
        }
        @POST
        @Path("/{algorithmname}")
        @Consumes({ "multipart/form-data" })
        @Produces({ "application/json" })
        @Operation(value = "", notes = "Train data on algorithm", response = void.class, tags={ "algorithm", })
        @ApiResponses(value = {
            @ApiResponse(responseCode = 200, message = "OK", response = void.class),
            @ApiResponse(responseCode = 400, message = "Bad Request", response = void.class),
            @ApiResponse(responseCode = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(responseCode = 403, message = "Forbidden", response = void.class),
            @ApiResponse(responseCode = 404, message = "Resource Not Found", response = void.class) })
        public Response algorithmAlgorithmnamePost(@Parameter(value = "SMILES identifier or Nanoparticle URI or comma separated list of SMILES  identifiers or Nanoparticle URI", required=true)@FormDataParam("identifier")  String identifier
            ,@Parameter(value = "name of an algorithm",required=true) @PathParam("algorithmname") String algorithmname
            ,@Parameter(value = "authorization token" )@HeaderParam("subjectid") String subjectid
            ,@Context SecurityContext securityContext)
                throws javax.ws.rs.NotFoundException, NotFoundException {
                return delegate.algorithmAlgorithmnamePost(identifier,algorithmname,subjectid,securityContext);
        }

    */
    @GET
    @Produces({TEXT_URILIST, MediaType.APPLICATION_JSON})
    @Operation(summary = "Get a list of algorithms.",
        description = "Get a list of all available algorithms.", tags = {"algorithm"}
        ,extensions = {
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "/algorithm/generic")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "x-orn:Algorithm")}),
        @Extension(name = "orn:returns", properties = { @ExtensionProperty(name = "x-orn-@id",  value = "x-orn:AlgorithmList")}),}
    )
    @GroupedApiResponsesOk
    public Response algorithmGet(
        @Parameter(description = "authorization token") @HeaderParam("subjectid") String subjectid,
        @Context SecurityContext securityContext,
        @Context UriInfo uriInfo,
        @Context HttpHeaders headers)
        throws NotFoundException, IOException {
            String accept = headers.getRequestHeaders().getFirst("accept");
            return delegate.algorithmGet(accept, subjectid, securityContext, uriInfo);
    }

}