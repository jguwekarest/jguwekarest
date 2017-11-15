package io.swagger.api;

import io.swagger.annotations.*;
import io.swagger.api.factories.AlgorithmFactory;

import javax.annotation.Generated;
import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

@Path("/algorithm")

@io.swagger.annotations.Api(description = "the algorithm API")
@Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class Algorithm  {
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
    @Produces({ "text/uri-list" })
    @ApiOperation(value = "Get a list of all available algorithms.", notes = "Get a list of all algorithms.", response = void.class, tags={ "algorithm", },extensions = @Extension(name = "my-extension", properties = { @ExtensionProperty(name = "test1", value = "value1")}))
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
        @ApiResponse(code = 404, message = "Resource Not Found", response = void.class),
        @ApiResponse(code = 500, message = "Server Error", response = void.class) })
    public Response algorithmGet(@ApiParam(value = "requested Content-Type" ,required=true, allowableValues="text/uri-list, application/json")@HeaderParam("Accept") String accept
    ,@ApiParam(value = "authorization token" )@HeaderParam("subjectid") String subjectid
    ,@Context SecurityContext securityContext
    ,@Context UriInfo uriinfo)
            throws NotFoundException, IOException, io.swagger.api.NotFoundException {
            return delegate.algorithmGet(accept,subjectid,securityContext,uriinfo);
    }


}
