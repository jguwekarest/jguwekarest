package helper;

import io.swagger.api.Api;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.InputStream;

public class TestHelper {

    public static Client getClient(){
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);
        return  ClientBuilder.newClient(clientConfig);
    }

    public static String getArff(String filename) throws IOException {
        InputStream arffIO = Api.class.getClassLoader().getResourceAsStream(filename);
        return IOUtils.toString(arffIO, "UTF-8");
    }
}
