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

public class ApiImpl extends ApiService {

    /**
     * get a JSON representation of the API
     * @param securityContext SecurityContext
     * @param ui UriInfo
     * @return JSON representation of the API
     * @throws NotFoundException
     * @throws IOException
     */
    @Override
    public Response apiApiJsonGet(SecurityContext securityContext, UriInfo ui) throws NotFoundException, IOException {
        InputStream in = new URL( ui.getBaseUri() + "openapi/openapi.json" ).openStream();

        String jsonString;
        try {
            jsonString = IOUtils.toString(in, "UTF-8");
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
