package io.swagger.api;

import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Swagger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class Bootstrap extends HttpServlet {
  @Override
  public void init(ServletConfig config) throws ServletException {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("type", "H2020");
    map.put("name", "OpenRiskNet");
    map.put("cordis", "http://www.cordis.europa.eu/project/rcn/206759_en.html");

    final Map<String, Object> contextmap = new HashMap<String, Object>();
    contextmap.put("@vocab", "http://schema.org/");
    contextmap.put("email", "email");

    Info info = new Info()
      .title("JGU WEKA REST Service")
      .description("RESTful API Webservice to WEKA Machine Learning Algorithms.\n" +
              "This webservice provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA Java Library.\n" +
              "This application is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de) at the Johannes Gutenberg University Mainz.\n" +
              "OpenRiskNet is funded by the European Commission GA 731075. WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.")
      .termsOfService("")
      .contact(new Contact()
        .email("rautenberg@uni-mainz.de"))
      .license(new License()
        .name("GNU General Public License 3")
        .url("https://www.gnu.org/licenses/gpl-3.0.de.html"))
      .version("0.0.3");
    info.setVendorExtension("x-project", map);
    info.setVendorExtension("@context", contextmap);

    ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);

    //swagger.securityDefinition("subjectid", new ApiKeyAuthDefinition("subjectid", In.HEADER));

    new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
  }
}
