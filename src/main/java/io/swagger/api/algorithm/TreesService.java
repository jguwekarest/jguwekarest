package io.swagger.api.algorithm;

import io.swagger.api.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.ServletContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public abstract class TreesService {
    public abstract Response algorithmJ48Post(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                              Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds,
                                              Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned,
                                              Integer useLaplace, Boolean save, SecurityContext securityContext, ServletContext servletContext,
                                              HttpHeaders headers)throws NotFoundException, IOException;
}
