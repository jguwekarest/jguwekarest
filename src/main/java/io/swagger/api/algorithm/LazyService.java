package io.swagger.api.algorithm;

import io.swagger.api.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;

public abstract class LazyService {
    public abstract Response algorithmKNNclassificationPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri,
                                                            Integer windowSize, Integer KNN, Integer crossValidate, String distanceWeighting, Integer meanSquared,
                                                            String nearestNeighbourSearchAlgorithm, String subjectid, HttpHeaders headers, UriInfo ui
                                      ) throws NotFoundException, IOException;
}
