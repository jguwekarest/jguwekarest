package io.swagger.api;


import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

@Path("/")

public class IndexPage {

        @Context ServletContext servletContext;
        @Context UriInfo ui;
        @Context HttpServletRequest servletRequest;
        // This method is called if HTML is request
        @GET
        @Produces("text/html")
        public String startPage() throws IOException {
            String baseuri = ui.getBaseUri().toString();
            InputStream in = new URL( baseuri  + "swagger.json" ).openStream();
            String jsonContent;
            try {
                jsonContent = new String(IOUtils.toString(in,"UTF-8"));
            } finally {
                IOUtils.closeQuietly(in);
            }

            JSONObject apiObject  = new JSONObject(jsonContent);
            //sort the JSONObject somehow
            String output = new String("<html><header><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></header><body><h1>list of services</h1><table>");
            output += "<tr><th>URI</th><th>REST method</th><th>Algorithm description</th></tr>";
            JSONObject paths = (JSONObject) apiObject.getJSONObject("paths");
            Iterator<?> keys = paths.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                System.out.println(key);
                if ( paths.get(key) instanceof JSONObject ) {
                    JSONObject method = (JSONObject) paths.get(key);
                    Iterator<?> methodKeys =  method.keys();
                    while( methodKeys.hasNext() ) {
                        String methodVal = (String)methodKeys.next();
                        output += "<tr><td><a href=\"" + StringUtil.removeTrailingSlash(baseuri) + key + "\">" + key + "</a></td><td>" + methodVal.toUpperCase() + "</td>";
                        JSONObject desc = (JSONObject) method.get(methodVal);
                        System.out.println(desc.get("description"));
                        output += "<td>" + desc.get("description") + "</td></tr>";
                    }
                }
            }
            output += "</table></body></html>";
            return output;
        }
}

