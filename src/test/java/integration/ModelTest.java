package integration;

import io.swagger.api.data.ModelService;
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

    @Test(description = "Post an arff file to BayesNet algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmBayesNetPost( @Optional  String host) throws Exception {

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

    @Test(description = "Post an arff file to BayesNet algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmBayesNetPostAndPredict( @Optional  String host) throws Exception {

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
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String model_uri = response.readEntity(String.class);

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        Assert.assertTrue(model_uri.matches(host + "/model/[0-9,a-f,A-F]{24}"));

        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("overcast,83,86,FALSE,yes,0.0"));
        Assert.assertTrue(prediction_text.contains("sunny,75,70,TRUE,yes,1.0"));
        Assert.assertFalse(prediction_text.contains("overcast,72,90,FALSE,yes,1.0"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);

    }

    @Test(description = "Try to delete with a none existing model id")
    public void deleteModelFalse() throws Exception {
        String id = "1234567890abcdef12345678";
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
    }



}
