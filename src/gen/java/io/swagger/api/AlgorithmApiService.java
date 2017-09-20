package io.swagger.api;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public abstract class AlgorithmApiService {
    public abstract Response algorithmAlgorithmnameGet(String accept,String algorithmname,String subjectid,SecurityContext securityContext,ServletContext servletContext) throws NotFoundException, IOException;
    public abstract Response algorithmAlgorithmnamePost(String identifier,String algorithmname,String subjectid,SecurityContext securityContext) throws NotFoundException;
    public abstract Response algorithmGet(String accept,String subjectid,SecurityContext securityContext) throws NotFoundException;
    public abstract Response algorithmBayesNetPost(InputStream fileInputStream, FormDataContentDisposition fileDetail,String estimator,BigDecimal estimatorParams,Integer useADTree,String searchAlgorithm,String searchParams,SecurityContext securityContext) throws NotFoundException, IOException;
    public abstract Response algorithmKNNclassificationPost(String predictionFeature,String datasetUri,String datasetService,Integer windowSize,Integer KNN,Integer crossValidate,Integer distanceWeighting,Integer meanSquared,String nearestNeighbourSearchAlgorithm,String subjectid,SecurityContext securityContext) throws NotFoundException;
}
