package integration;

import helper.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicTest {

    private static final Logger LOG = Logger.getLogger(BasicTest.class.getName());

    @Test()
    @Parameters({"host"}) // variable host from testng.xml
    public void indexGet( @Optional  String host) throws Exception {

        Client client = TestHelper.getClient();
        WebTarget webTarget = client.target(host);
        Invocation.Builder request = webTarget.request(MediaType.TEXT_HTML);
        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals(MediaType.TEXT_HTML));

    }

    @Test()
    @Parameters({"host"})
    public void swaggerJSONGet( @Optional  String host) throws Exception {

        Client client = TestHelper.getClient();
        WebTarget webTarget = client.target(host + "/swagger.json");
        Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals(MediaType.APPLICATION_JSON));

    }

    @Test()
    @Parameters({"host"})
    public void dataJSONGet( @Optional  String host) throws Exception {

        Client client = TestHelper.getClient();
        String[]  arr = { "dataset", "model" };

        for (String subdir : arr) {
            WebTarget webTarget = client.target(host + "/" + subdir);
            Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON);
            Response response = request.get();
            Assert.assertTrue(response.getStatus() == 200, "Data at host: " + host + "/" + subdir + " not available.");
            Assert.assertTrue(response.getMediaType().toString().equals(MediaType.APPLICATION_JSON), "Data at host: " + host + "/" + subdir + " not available in mime-type JSON.");
            LOG.log(Level.INFO, "Datatest: " + subdir);
        }

    }



}