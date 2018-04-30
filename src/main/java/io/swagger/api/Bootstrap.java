package io.swagger.api;

import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.ServletOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class to init the servlet and set Swagger/OpenAPI Info
 */
public class Bootstrap extends HttpServlet {
  @Override
  public void init(ServletConfig config) throws ServletException {


    //((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
    //((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.cluster").setLevel(Level.ERROR);
    final Map<String, Object> map = new HashMap< >();
    map.put("type", "H2020");
    map.put("name", "OpenRiskNet");
    map.put("cordis", "http://www.cordis.europa.eu/project/rcn/206759_en.html");

    final Map<String, Object> contextmap = new HashMap< >();
    contextmap.put("@vocab", "http://schema.org/");


    OpenAPI oas = new OpenAPI();
    SwaggerConfiguration oasConfig = new SwaggerConfiguration()
        .openAPI(oas)
        .prettyPrint(true)
        .resourcePackages(Stream.of("io.swagger.api").collect(Collectors.toSet()));

    try {
      new JaxrsOpenApiContextBuilder()
          .servletConfig(config)
          .openApiConfiguration(oasConfig)
          .buildContext(true);
    } catch (OpenApiConfigurationException e) {
      throw new ServletException("Bootstrap Error: " + e.getMessage(), e);
    } catch (Exception e){
      e.printStackTrace();
    }


    try {
      OpenAPI openAPI = new ServletOpenApiContextBuilder()
          .servletConfig(config)
          .buildContext(true)
          .read();
    } catch (OpenApiConfigurationException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

 /*



    @OpenAPIDefinition(
        info = @Info(
        title = "")
    )




    Info info = new Info()
      .title("JGU WEKA REST Service")
      .description("RESTful API Webservice to WEKA Machine Learning Algorithms.\n" +
              "This webservice provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA Java Library.\n" +
              "This application is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de) at the Johannes Gutenberg University Mainz.\n" +
              "OpenRiskNet is funded by the European Commission GA 731075. WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.\n" +
              "See [Documentation](https://jguwekarest.github.io/jguwekarest/), [Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) and [Code](https://github.com/jguwekarest/jguwekarest) at Github.")
      //.termsOfService("")
      .contact(new Contact()
        .email("rautenberg@uni-mainz.de"))
      .license(new License()
        .name("GNU General Public License 3")
        .url("https://www.gnu.org/licenses/gpl-3.0.de.html"))
      .version("0.1.0");
    info.setVendorExtension("x-orn-@project", map);
    info.setVendorExtension("x-orn-@context", contextmap);

    //ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);
    swagger.setVendorExtension("x-orn-@type", "x-orn:Service");
    swagger.setVendorExtension("x-orn-@id", "weka rest service");

    //swagger.securityDefinition("subjectid", new ApiKeyAuthDefinition("subjectid", In.HEADER));

    new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
    */
  }
}
