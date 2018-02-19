package io.swagger.api.algorithm;

import io.swagger.api.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public abstract class TreesService {
    public abstract Response algorithmJ48Post(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                              Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds,
                                              Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned,
                                              Integer useLaplace, String subjectid, HttpHeaders headers, UriInfo uriInfo
                                              )throws NotFoundException, IOException;
    public abstract Response algorithmJ48AdaBoostPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                              Integer batchSize, Integer numIterations, Integer useResampling, Integer weightThreshold,
                                              Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds,
                                              Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned,
                                              Integer useLaplace, String subjectid, HttpHeaders headers, UriInfo uriInfo
                                              )throws NotFoundException, IOException;
    public abstract Response algorithmJ48BaggingPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, Integer bagSizePercent,
                                              Integer batchSize, Integer numIterations, Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj,
                                              Integer numFolds, Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned,
                                              Integer useLaplace, String subjectid, HttpHeaders headers, UriInfo uriInfo
                                              )throws NotFoundException, IOException;
}
