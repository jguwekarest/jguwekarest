package io.swagger.api.algorithm;

import io.swagger.api.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public abstract class RulesService {
    public abstract Response algorithmZeroRPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Boolean save,
                                                String subjectid, SecurityContext securityContext) throws NotFoundException, IOException;
    public abstract Response algorithmM5RulesPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                                  Integer unpruned, Integer useUnsmoothed, Double minNumInstances, Integer buildRegressionTree,
                                                  Boolean save, String subjectid, SecurityContext securityContext
                                                 ) throws NotFoundException, IOException;
}