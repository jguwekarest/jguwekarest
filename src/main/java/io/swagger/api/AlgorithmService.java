package io.swagger.api;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

public abstract class AlgorithmService {
    public abstract Response algorithmAlgorithmnameGet(String accept,String algorithmname,String subjectid,SecurityContext securityContext,ServletContext servletContext) throws NotFoundException, IOException;
    public abstract Response algorithmAlgorithmnamePost(String identifier,String algorithmname,String subjectid,SecurityContext securityContext) throws NotFoundException;
    public abstract Response algorithmGet(String accept, String subjectid, SecurityContext securityContext, UriInfo uriInfo) throws NotFoundException,IOException;
}
