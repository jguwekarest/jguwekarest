package io.swagger.api.algorithm;

import io.swagger.api.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public abstract class FunctionsService {
    public abstract Response linearRegressionPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, /* String predictionFeature, String datasetUri, String datasetService, */ Integer attributeSelectionMethod, Integer eliminateColinearAttributes, BigDecimal ridge, String subjectid, SecurityContext securityContext) throws NotFoundException, IOException;
}
