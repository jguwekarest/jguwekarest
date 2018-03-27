package io.swagger.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Swagger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class Bootstrap extends HttpServlet {
  @Override
  public void init(ServletConfig config) throws ServletException {

    ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
    ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.cluster").setLevel(Level.ERROR);
    final Map<String, Object> map = new HashMap< >();
    map.put("type", "H2020");
    map.put("name", "OpenRiskNet");
    map.put("cordis", "http://www.cordis.europa.eu/project/rcn/206759_en.html");

    final Map<String, Object> contextmap = new HashMap< >();
    contextmap.put("@vocab", "http://schema.org/");

    Info info = new Info()
      .title("JGU WEKA REST Service")
      .description("RESTful API Webservice to WEKA Machine Learning Algorithms.\n" +
              "This webservice provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA Java Library.\n" +
              "This application is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de) at the Johannes Gutenberg University Mainz.\n" +
              "OpenRiskNet is funded by the European Commission GA 731075. WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.\n" +
              "See [Documentation](https://jguwekarest.github.io/jguwekarest/), [Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) and [Code](https://github.com/jguwekarest/jguwekarest) at Github.")
      //.termsOfService("")
      .contact(new Contact()
        .email("rautenberg@uni-mainz.de?subject=JGU WEKA REST Service"))
      .license(new License()
        .name("GNU General Public License 3")
        .url("https://www.gnu.org/licenses/gpl-3.0.de.html"))
      .version("0.0.5");
    info.setVendorExtension("x-orn-@project", map);
    info.setVendorExtension("x-orn-@context", contextmap);

    //ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);
    swagger.setVendorExtension("x-orn-@type", "x-orn:Service");
    swagger.setVendorExtension("x-orn-@id", "weka rest service");

    //swagger.securityDefinition("subjectid", new ApiKeyAuthDefinition("subjectid", In.HEADER));

    new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
  }
}
