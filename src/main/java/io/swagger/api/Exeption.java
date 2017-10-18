package io.swagger.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class Exeption {

    public static class AAException extends Exception{
        private int code;
        public AAException(int code, String msg) {
            super("AAException Error: " + msg);
            this.code = code;
        }


    }


}
