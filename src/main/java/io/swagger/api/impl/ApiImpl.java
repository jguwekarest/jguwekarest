package io.swagger.api.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.api.ApiService;
import io.swagger.api.NotFoundException;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class ApiImpl extends ApiService {

    @Override
    public Response apiApiJsonGet(SecurityContext securityContext, UriInfo ui) throws NotFoundException, IOException {



        InputStream in = new URL( ui.getBaseUri() + "swagger.json" ).openStream();
        String jsonString;
        try {
            jsonString = new String(IOUtils.toString(in, "UTF-8"));
        } finally {
            IOUtils.closeQuietly(in);
        }

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return Response.ok(prettyJson).build();

    }
}
