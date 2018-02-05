package integration;

import io.swagger.api.authorization.AuthorizationService;
import io.swagger.api.data.DatasetService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;

public class DatasetTest {


    private static final String EXT_DATASET = "http://dev.jaqpot.org:8081/jaqpot/services/dataset/yzsAXE5rLPzz";

    @Parameters({"host"})
    @Test(description = "Get an external dataset and convert it to arff.")
    public void loadExternalDatasetToArff( @Optional  String host ) throws Exception {
        String uri = host + "/dataset";
        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        String token = AuthorizationService.login("guest","guest");

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("dataset_uri", EXT_DATASET)
                         .field("subjectid", token);

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart;

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/x-arff");
        request.header("subjectid", token);

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String dataset = response.readEntity(String.class);
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(dataset.contains("meta contributors: Yoram Cohen, Hongbo Guo,"));
        Assert.assertTrue(dataset.contains("0.147,22.64,-24.08,197.0,44.43,35.03,4.79,0.022,35.03,-5.505,0.0,-6.73,0.224,22.32,14.9,0.275,0.0,247.019,10.0,0.0,63.72,19.1,518.33,0.184,34.07,44.8"));

    }

    @Parameters({"host"})
    @Test(description = "Get an external dataset, ..., save it and delete it.")
    public void loadExternalDatasetToArffSaveAndDelete( @Optional  String host ) throws Exception {
        String uri = host + "/dataset";
        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        String token = AuthorizationService.login("guest","guest");

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("dataset_uri", EXT_DATASET)
                .field("subjectid", token);

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart;

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");
        request.header("subjectid", token);

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String dataset_uri = response.readEntity(String.class);
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        System.out.println("Dataset URI of the saved external dataset is: " + dataset_uri);
        Assert.assertTrue(dataset_uri.matches(host + "/dataset/[0-9,a-f,A-F]{24}"));
        String id = dataset_uri.substring(dataset_uri.length() - 24);
        Boolean resultDelete = DatasetService.deleteDataset(id);
        Assert.assertTrue(resultDelete);

    }

    @Test(description = "Try to delete with a none existing model id")
    public void deleteDatasetFalse() throws Exception {
        String id = "1234567890abcdef12345678";
        Boolean resultDelete = DatasetService.deleteDataset(id);
        Assert.assertTrue(resultDelete);
    }


}
