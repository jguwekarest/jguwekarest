package io.swagger.api.impl;

import io.swagger.api.NotFoundException;
import io.swagger.api.algorithm.MetaService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;

public class MetaImpl extends MetaService {
    @Override
    @Produces("text/plain")
    public Response algorithmAdaBoostPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                                   String subjectid, HttpHeaders headers, UriInfo uriInfo)
            throws NotFoundException, IOException {

                return null;

    }


}
