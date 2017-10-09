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

public class Bootstrap extends HttpServlet {
  @Override
  public void init(ServletConfig config) throws ServletException {
    Info info = new Info()
      .title("JGU WEKA REST services")
      .description("REST API webservice for weka.")
      .termsOfService("")
      .contact(new Contact()
        .email("rautenberg@uni-mainz.de"))
      .license(new License()
        .name("GNU General Public License 3")
        .url("https://www.gnu.org/licenses/gpl-3.0.de.html"));

    ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);

    new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
  }
}
