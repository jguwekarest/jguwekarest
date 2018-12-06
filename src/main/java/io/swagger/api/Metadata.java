package io.swagger.api;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

/**
 * Metadata Class to add info-object and OpenRiskNet JSON-LD annotations to the OpenAPI representation.
 *
 */
@OpenAPIDefinition(

    extensions = {
        @Extension(name = "orn-@context", properties = {
            @ExtensionProperty(name = "@vocab", value = "http://openrisknet.org/schema#"),
            @ExtensionProperty(name = "orn",  value = "http://openrisknet.org/schema#"),
            @ExtensionProperty(name = "orn-@id",  value = "@id"),
            @ExtensionProperty(name = "orn-@type",  value = "@type")
        }),
        @Extension(properties = {@ExtensionProperty(name = "orn-@type",  value = "Service")}),
        @Extension(properties = {@ExtensionProperty(name = "orn-@id",  value = "https://jguweka.prod.openrisknet.org")})
    },
    info = @Info(
        title = "JGU WEKA REST Service",
        version = "0.5.0-OAS3",
        description = "RESTful API Webservice to WEKA Machine Learning Algorithms.\n" +
            "  This webservice provides an [OpenRiskNet](https://openrisknet.org/) compliant REST interface to machine learning algorithms from the WEKA Java Library.\n" +
            "  This application is developed by the [Institute of Computer Science](http://www.datamining.informatik.uni-mainz.de) at the Johannes Gutenberg University Mainz.\n\n" +
            "  OpenRiskNet is funded by the European Commission GA 731075. WEKA is developed by the [Machine Learning Group](https://www.cs.waikato.ac.nz/ml/index.html) at the University of Waikato.\n\n" +
            "  See [Documentation](https://jguwekarest.github.io/jguwekarest/), [Issue Tracker](https://github.com/jguwekarest/jguwekarest/issues) and [Code](https://github.com/jguwekarest/jguwekarest) at Github.",
        license = @License(name = "GNU General Public License 3", url = "https://www.gnu.org/licenses/gpl-3.0.de.html"),
        contact = @Contact(url = "http://www.datamining.informatik.uni-mainz.de", name = "Data Mining Group JGU Mainz", email = "rautenberg@uni-mainz.de")
    ),
    externalDocs = @ExternalDocumentation(description = "JGU WEKA REST Service Documentation on GitHub", url = "https://jguwekarest.github.io/jguwekarest/")

)


public class Metadata {

}

