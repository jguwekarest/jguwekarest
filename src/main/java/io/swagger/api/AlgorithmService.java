package io.swagger.api;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.ServletContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class AlgorithmService {
    public abstract Response algorithmAlgorithmnameGet(String accept,String algorithmname,String subjectid,SecurityContext securityContext,ServletContext servletContext) throws NotFoundException, IOException;
    public abstract Response algorithmAlgorithmnamePost(String identifier,String algorithmname,String subjectid,SecurityContext securityContext) throws NotFoundException;

    public abstract Response algorithmGenericPost(InputStream fileInputStream, String datasetUri,
                                           String classifierName, String paramString, String validation, Double validationNum,
                                           HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException;

    public abstract Response algorithmGenericGet(String classifierName, HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException;

    public abstract Response algorithmPost(InputStream fileInputStream, String datasetUri,
                                           String classifierName, HashMap params, String validation, Double validationNum,
                                           HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException;
    public abstract Response algorithmPost(InputStream fileInputStream, String datasetUri,
                                           String classifierName, HashMap params, String metaClassifierName, HashMap metaParams,
                                           String validation, Double validationNum,
                                           HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException;
    public abstract Response algorithmPost(InputStream fileInputStream, String datasetUri,
                                           String classifierName, HashMap params, String metaClassifierName, HashMap metaParams, String paramString,
                                           String validation, Double validationNum,
                                           HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException;
    public abstract Response algorithmGet(String accept, String subjectid, SecurityContext securityContext, UriInfo uriInfo) throws NotFoundException,IOException;
}
