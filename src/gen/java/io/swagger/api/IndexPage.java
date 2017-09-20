package io.swagger.api;


import org.glassfish.jersey.message.XmlHeader;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@Path("/")

public class IndexPage {

        @Context  ServletContext servletContext;
        // This method is called if HTML is request
        @GET
        @Produces("text/html")
        @XmlHeader("<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>")
        public String startPage() throws IOException {
            String contextBasePath = new String(servletContext.getRealPath("/"));
            String content = new String(Files.readAllBytes(Paths.get(contextBasePath +"/index.jsp")));
            String jsonContent = new String(Files.readAllBytes(Paths.get(contextBasePath +"/swagger.json")));

            JSONObject apiObject  = new JSONObject(jsonContent);;

            String output = new String("<html><header><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></header><body><h1>list of services</h1><table>");
            output += "<tr><th>URI</th><th>REST method</th><th>Algorithm description</th></tr>";
            JSONObject paths = (JSONObject) apiObject.getJSONObject("paths");
            System.out.println(paths);
            Iterator<?> keys = paths.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                System.out.println(key);
                if ( paths.get(key) instanceof JSONObject ) {
                    JSONObject method = (JSONObject) paths.get(key);
                    Iterator<?> methodKeys =  method.keys();
                    while( methodKeys.hasNext() ) {
                        String methodVal = (String)methodKeys.next();
                        output += "<tr><td><a href=\"" + key + "\">" + key + "</a></td><td>" + methodVal.toUpperCase() + "</td>";
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

