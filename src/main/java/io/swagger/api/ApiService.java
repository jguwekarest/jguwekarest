package io.swagger.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

public abstract class ApiService {
    public abstract Response apiApiJsonGet(SecurityContext securityContext, UriInfo ui) throws NotFoundException, IOException;
}