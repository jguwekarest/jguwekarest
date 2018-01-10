package integration;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.io.File;

public class ModelTest {
    @Test()
    @Parameters({"host"})
    public void algorithmBayesNetPost( @Optional  String host) throws Exception {
        //  -H  "accept: text/x-arff" -H  "Content-Type: multipart/form-data" -F "file=@weather.numeric.arff;type=" -F "estimator=SimpleEstimator" -F "estimatorParams=0.5" -F "useADTree=0" -F "searchAlgorithm=local.K2" -F "searchParams=-P 1 -S BAYES"
        String uri = host + "/algorithm/BayesNet";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("estimator", "SimpleEstimator")
                         .field("estimatorParams", "0.5")
                         .field("useADTree", "0")
                         .field("searchAlgorithm", "local.K2")
                         .field("searchParams", "-P 1 -S BAYES");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("save", "false").bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/x-arff");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/x-arff"));

    }




}
