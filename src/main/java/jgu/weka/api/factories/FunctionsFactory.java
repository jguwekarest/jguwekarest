package jgu.weka.api.factories;


import jgu.weka.api.algorithm.FunctionsService;
import jgu.weka.api.impl.FunctionsImpl;

public class FunctionsFactory {
    private final static FunctionsService service = new FunctionsImpl();

    public static FunctionsService getFunctions() {
        return service;
    }
}
