package io.swagger.api.factories;

import io.swagger.api.impl.TreesImpl;
import io.swagger.api.algorithm.TreesService;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class TreesFactory {
    private final static TreesService service = new TreesImpl();
    public static TreesService getTrees() {
        return service;
    }
}