package jgu.weka.api.factories;

import jgu.weka.api.algorithm.TreesService;
import jgu.weka.api.impl.TreesImpl;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class TreesFactory {
    private final static TreesService service = new TreesImpl();

    public static TreesService getTrees() {
        return service;
    }

}