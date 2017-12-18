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
    public abstract Response linearRegressionPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                        Integer attributeSelectionMethod, Integer eliminateColinearAttributes, BigDecimal ridge,
                                        Boolean save, String subjectid, SecurityContext securityContext
                                        ) throws NotFoundException, IOException;
    public abstract Response libSVMPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer svmType,
                                        Float coef0, Float cost, Integer degree, BigDecimal eps, BigDecimal gamma, Integer kernelType,
                                        BigDecimal loss, Boolean normalize, BigDecimal nu, Boolean probabilityEstimates, Boolean shrinking,
                                        String weights, Boolean save, String subjectid, SecurityContext securityContext
                                        ) throws NotFoundException, IOException;
}
