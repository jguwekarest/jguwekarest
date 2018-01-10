package integration;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class BasicTest {

    @Test()
    @Parameters({"host"})
    public void indexGet( @Optional  String host) throws Exception {

        host = (host != null ? host : "http://0.0.0.0:8081");
        Client client = getClient();
        WebTarget webTarget = client.target(host);
        Invocation.Builder request = webTarget.request(MediaType.TEXT_HTML);
        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals(MediaType.TEXT_HTML));

    }

    @Test()
    @Parameters({"host"})
    public void swaggerJSONGet( @Optional  String host) throws Exception {

        host = (host != null ? host : "http://0.0.0.0:8081") + "/swagger.json";
        Client client = getClient();
        WebTarget webTarget = client.target(host);
        Invocation.Builder request = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals(MediaType.APPLICATION_JSON));

    }

    public Client getClient(){
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);
        return client;
    }

}