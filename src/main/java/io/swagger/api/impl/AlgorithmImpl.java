package io.swagger.api.impl;

import io.swagger.api.AlgorithmService;
import io.swagger.api.ApiResponseMessage;
import io.swagger.api.NotFoundException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class AlgorithmImpl extends AlgorithmService {

    @Override
    public Response algorithmAlgorithmnameGet(String accept, String algorithmname, String subjectid, SecurityContext securityContext, ServletContext servletContext) throws IOException {
        // do some magic!
        String jsonString;
        try {
            String contextBasePath = new String(servletContext.getRealPath("/"));
            jsonString = new String(Files.readAllBytes(Paths.get(contextBasePath + "/swagger.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Response.ok(jsonString).build();
    }
    @Override
    public Response algorithmAlgorithmnamePost(String identifier, String algorithmname, String subjectid, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response algorithmGet(String accept, String subjectid, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }


}
