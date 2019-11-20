package org.kramerlab.wekarestapi;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

/**
 * Metadata Class to add info-object and OpenRiskNet JSON-LD
 * annotations to the OpenAPI representation.
 */
@OpenAPIDefinition(extensions = {@Extension(name = "orn-@context",
                                            properties = {@ExtensionProperty(name = "@vocab",
                                                                             value = "http://openrisknet.org/schema#"),
                                                          @ExtensionProperty(name = "x-orn",
                                                                             value = "http://openrisknet.org/schema#"),
                                                          @ExtensionProperty(name = "x-orn-@id",
                                                                             value = "@id"),
                                                          @ExtensionProperty(name = "x-orn-@type",
                                                                             value = "@type")}),
                                 @Extension(properties = {@ExtensionProperty(name = "orn-@type",
                                                                             value = "Service")}),
                                 @Extension(properties = {@ExtensionProperty(name = "orn-@id",
                                                                             value = "https://jguweka.prod.openrisknet.org")})},
                   info = @Info(title = "JGU WEKA REST Service", version = "0.5.1-OAS3",
                                description = "This is a RESTful API Webservice for WEKA Machine "
                                              + "Learning algorithms.\nThis webservice provides an "
                                              + "[OpenRiskNet](https://openrisknet.org/) compliant "
                                              + "REST interface for machine learning algorithms "
                                              + "from the WEKA library.\n This service is developed "
                                              + "by the [Institute of Computer Science]"
                                              + "(http://www.datamining.informatik.uni-mainz.de) "
                                              + "at the Johannes Gutenberg University Mainz.\n\n"
                                              + "OpenRiskNet is funded by the European Commission "
                                              + "GA 731075.\nWEKA is developed by the [Machine "
                                              + "Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) "
                                              + "at the University of Waikato.\n\n"
                                              + "See [Documentation](https://jguwekarest.github.io/jguwekarest/), "
                                              + "[Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) "
                                              + "and [Code](https://github.com/jguwekarest/jguwekarest) at Github.",
                                license = @License(name = "GNU General Public License 3",
                                                   url = "https://www.gnu.org/licenses/gpl-3.0.html"),
                                contact = @Contact(url = "https://www.datamining.informatik.uni-mainz.de",
                                                   name = "Data Mining Group, Institute of Computer Science, J.G.U. Mainz",
                                                   email = "datamining@uni-mainz.de")),
                   externalDocs = @ExternalDocumentation(description = "JGU WEKA REST Service Documentation on GitHub",
                                                         url = "https://jguwekarest.github.io/jguwekarest/")
)

public class Metadata {
    
}
