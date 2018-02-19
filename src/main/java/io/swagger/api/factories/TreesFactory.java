package io.swagger.api.factories;

import io.swagger.api.impl.TreesImpl;
import io.swagger.api.algorithm.TreesService;


public class TreesFactory {
    private final static TreesService service = new TreesImpl();
    public static TreesService getTrees() {
        return service;
    }
}