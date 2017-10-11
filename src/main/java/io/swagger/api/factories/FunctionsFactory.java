package io.swagger.api.factories;


import io.swagger.api.algorithm.FunctionsService;
import io.swagger.api.impl.FunctionsImpl;

public class FunctionsFactory {
    private final static FunctionsService service = new FunctionsImpl();

    public static FunctionsService getFunctions() {
        return service;
    }
}
