package unit;


import io.swagger.api.Api;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.lang.annotation.Annotation;

import static org.testng.Assert.assertEquals;

//import org.glassfish.jersey.media.multipart.FormDataParam;

public class AnnotationTest {


    /*
    //@Test
    public void checkSwaggerInfo() throws Exception{

        Annotation annotation = SwaggerDefinition.Scheme.class.getAnnotation(Info.class);
System.out.println("1: " + annotation.toString());
        System.out.println("2: " + annotation.annotationType());
        if(annotation instanceof Info){
            Info info = (Info) annotation;
            assertEquals("JGU WEKA REST Service", info.title());
            assertEquals("0.0.3", info.version());
        } else {
            System.out.println("Did not find annotation");
        }
    }
*/

   // @Test
    public void checkLibSVMAnnotation() throws Exception{

        Annotation annotation = Api.class.getAnnotation(FormDataParam.class);

        if(annotation instanceof FormDataParam){
            FormDataParam param = (FormDataParam) annotation;
            assertEquals("coef0", param.value());
            //assertEquals("", info.version());
        } else {
            System.out.println("Did not find annotation");
        }
    }
}
