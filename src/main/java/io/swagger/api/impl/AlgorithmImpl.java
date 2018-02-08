package io.swagger.api.impl;

import io.swagger.api.AlgorithmService;
import io.swagger.api.ApiResponseMessage;
import io.swagger.api.NotFoundException;
import io.swagger.api.StringUtil;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class AlgorithmImpl extends AlgorithmService {

    @Override
    public Response algorithmAlgorithmnameGet(String accept, String algorithmname, String subjectid, SecurityContext securityContext, ServletContext servletContext) throws IOException {
        // do some magic!
        String jsonString;
        try {
            String contextBasePath = new String(servletContext.getRealPath("/"));
            jsonString = new String(Files.readAllBytes(Paths.get(contextBasePath + "/swagger.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Response.ok(jsonString).build();
    }

    @Override
    public Response algorithmAlgorithmnamePost(String identifier, String algorithmname, String subjectid, SecurityContext securityContext) throws NotFoundException {
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response algorithmGet(String accept, String subjectid, SecurityContext securityContext, UriInfo ui) throws NotFoundException, IOException {
        String baseuri = ui.getBaseUri().toString();
        InputStream in = new URL( ui.getBaseUri() + "swagger.json" ).openStream();
        String jsonContent;
        try {
            jsonContent = new String(IOUtils.toString(in, "UTF-8"));
        } finally {
            IOUtils.closeQuietly(in);
        }

        JSONObject apiObject  = new JSONObject(jsonContent);
        //sort the JSONObject somehow
        String output = new String("");
        JSONObject paths = (JSONObject) apiObject.getJSONObject("paths");
        JSONObject jsonout = new JSONObject();

        Iterator<?> keys = paths.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if ( paths.get(key) instanceof JSONObject ) {
                JSONObject method = (JSONObject) paths.get(key);
                Iterator<?> methodKeys =  method.keys();
                while( methodKeys.hasNext() ) {
                    String methodVal = (String)methodKeys.next();
                    if (!key.contains("{") && key.startsWith("/algorithm/")) {
                        output += "" + StringUtil.removeTrailingSlash(baseuri) + key + "\n";
                        if (accept.equals(MediaType.APPLICATION_JSON)) jsonout.put( StringUtil.removeTrailingSlash(baseuri) + key, paths.getJSONObject(key));
                    }
                }
            }
        }

        if (accept.equals(MediaType.APPLICATION_JSON)) return Response.ok(jsonout.toString()).build();
        return Response.ok(output).build();
    }


}
